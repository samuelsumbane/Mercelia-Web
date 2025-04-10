package view.modules.productsModule

import androidx.compose.runtime.*
import components.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.dom.*
import repository.*


@Composable
fun categoriesPage() {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val categories = CategoryRepository(httpClient)
    val users = UserRepository(httpClient)

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
    var isLoggedIn by remember { mutableStateOf(false) }


    fun cleanFormFields() {
        categoryName = ""
        categoryNameError = ""
    }

    LaunchedEffect(Unit) {
        isLoggedIn = users.checkSession()

        if (isLoggedIn) {
            try {
                categoriesData = categories.getCategories()
            } catch (e: Exception) {
                error = "Error: ${e.message}"
            }
        }
    }

    if (isLoggedIn) {
        NormalPage(
            title = "Categorias",
            pageActivePath = "sidebar-btn-products",
            hasMain = true,
            hasNavBar = true,
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
                            showDeleteBtn = false
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
                                // Save afiliate --------->
                                if (categoryId != 0) {
                                    val status = categories.editCategory(CategoryItem(categoryId, categoryName))
                                    if (status == 201) alert("success", "Sucesso", "Categoria actualizada com sucesso.")
                                    modalState = "closed"
                                } else {
                                    val status = categories.createCategory(CategoryItem(null, categoryName))
                                    if (status == 201) alert("success", "Sucesso!", "Categoria adicionada com sucesso.")
                                }
                                categoryName = ""
                            }
                            //                        }
                        }
                    }
                ) {

                    formDiv(
                        "Nome", categoryName, InputType.Text,
                        onInput = { event -> categoryName = event.value },
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
    } else userNotLoggedScreen()
}
