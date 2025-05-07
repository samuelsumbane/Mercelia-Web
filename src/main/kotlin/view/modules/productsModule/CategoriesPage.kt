package view.modules.productsModule

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.InputType
//import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.dom.*
import repository.*


@Composable
fun categoriesPage(userRole: String, sysPackage: String) {

    val router = Router.current
    val categories = CategoryRepository()
    val commonRepo = CommonRepository()

    var categoriesData by remember { mutableStateOf<List<CategoryItem>?>(null) }

    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var modalTitle by remember { mutableStateOf("") }
    var modalState by remember { mutableStateOf("closed") } //closed = "" --------->>
//    var modalState by remember { mutableStateOf("open-min-modal") } //closed = "" --------->>
    var categoryId by remember { mutableStateOf(0) }
    var categoryName by remember { mutableStateOf("") }
    var categoryNameError by remember { mutableStateOf("") }
    var submitBtnText by remember { mutableStateOf("Submeter") }

    fun cleanFormFields() {
        categoryName = ""
        categoryNameError = ""
    }

    LaunchedEffect(Unit) {
        try {
            categoriesData = categories.getCategories()
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        }
    }

    NormalPage(
        showBackButton = true,
        onBackFunc = { router.navigate("/products-module") },
        title = "Categorias",
        pageActivePath = "sidebar-btn-products",
        sysPackage = sysPackage,
        hasMain = true,
        hasNavBar = true,
        userRole = userRole,
        navButtons = {
            button("btnSolid", "+ Categoria") {
                modalTitle = "Adicionar Categorias"
                modalState = "open-min-modal"
                submitBtnText = "Submeter"
                cleanFormFields()
            }

        }) {
        if (categoriesData != null) {
            categoriesData!!.forEach { item ->
                cardWG(title = "", cardButtons = {
                    cardButtons(
                        onEditButton = {
                            modalState = "open-min-modal"
                            categoryId = item.id!!
                            categoryName = item.name
                            submitBtnText = "Editar"
                        },
                        showDeleteBtn = true,
                        onDeleteButton = {
//                                console.log("deu certo1.")

                            alertDelete("Deletar está categoria?", "Está acção não pode ser desfeita.") {
                                coroutineScope.launch {
                                    // Delete category --------->
                                    val status = categories.deleteCategory(item.id!!)
                                    console.log(status)
                                    when (status) {
                                        200 -> {
                                            alertTimer("Categoria deletada com sucesso.")
                                            categoriesData = categories.getCategories()
                                        }
                                        406 -> alert("warning", "Delete não aceite", "O sistema deve ter pelo menos uma categoria")
                                    }
                                }
                            }
                        }
                    )
                }) {
                    H4 { Text(item.name) }
                }
            }
        } else if (error != null) {
            Div { Text(error!!) }
        } else {
            Div { Text("Carregando...") }
        }

        minModal(modalState, modalTitle) {
            Form(
                attrs = {
                    classes("modalform")
                    onSubmit { event ->
                        event.preventDefault()

                        if (categoryName.isBlank()) {
                            categoryNameError = "Nome da categoria é obrigatório"
                            return@onSubmit
                        } else categoryNameError = ""

                        coroutineScope.launch {
                            if (categoryId != 0) {
                                // Edit category --------->
                                val (status, message) = commonRepo.postRequest("$apiCategoriesPath/update-category", CategoryItem(categoryId, categoryName), "put")
                                if (status == 201) alertTimer("Categoria actualizada com sucesso.")
                                modalState = "closed"
                            } else {
                                // Save category --------->
                                val (status, message) = commonRepo.postRequest("$apiCategoriesPath/create-category", CategoryItem(null, categoryName))
                                if (status == 201) alertTimer("Categoria adicionada com sucesso.")
                            }
                            categoryName = ""
                        }
                    }
                }
            ) {

                formDiv(
                    "Nome", categoryName, InputType.Text, 98,
                    oninput = { event -> categoryName = event.value },
                    categoryNameError
                )

                submitButtons(submitBtnText = submitBtnText) {
                    modalState = "closed"
                    categoryName = ""
                    categoryId = 0
                    coroutineScope.launch { categoriesData = categories.getCategories() }
                    cleanFormFields()
                }
            }
        }
    }

}
