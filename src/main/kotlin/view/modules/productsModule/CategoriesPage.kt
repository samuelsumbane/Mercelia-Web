package view.modules.productsModule

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.dom.*
import repository.*
import view.state.UiState.modalState
import view.state.UiState.modalTitle
import view.state.UiState.submitBtnText
import view.state.AppState.error
import view.state.AppState.isLoading


@Composable
fun categoriesPage(userRole: String, sysPackage: String) {

    val router = Router.current
    val categories = CategoryRepository()
    val commonRepo = CommonRepository()
    var categoriesData by remember { mutableStateOf<List<CategoryItem>?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var categoryId by remember { mutableStateOf(0) }
    var categoryName by remember { mutableStateOf("") }
    var categoryNameError by remember { mutableStateOf("") }

    fun cleanFormFields() {
        categoryName = ""
        categoryNameError = ""
    }

    LaunchedEffect(Unit) {
        try {
            categoriesData = categories.getCategories()
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    if (isLoading) {
        loadingModal()
    } else {
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
                        if (!item.isDefault) {
                            cardButtons(
                                onEditButton = {
                                    modalState = "open-min-modal"
                                    modalTitle = "Editar categoria"
                                    categoryId = item.id!!
                                    categoryName = item.name
                                    submitBtnText = "Editar"
                                },
                                showDeleteBtn = true,
                                onDeleteButton = {
                                    alertDelete("Deletar está categoria?", "Está acção não pode ser desfeita.") {
                                        coroutineScope.launch {
                                            // Delete category --------->>
                                            val (status, message) = commonRepo.deleteRequest("$apiCategoriesPath/delete-category/${item.id!!}")
                                            when (status) {
                                                200 -> {
                                                    alertTimer(message)
                                                    categoriesData = categories.getCategories()
                                                }

                                                404 -> alert("error", message, "")
                                                406 -> alert("warning", "Delete não aceite", message)
                                                else -> unknownErrorAlert()
                                            }
                                        }
                                    }
                                }
                            )
                        }
                    }) {
                        CardPitem("Categoria", item.name)
                    }
                }
            } else if (error != null) {
                Div { Text(error!!) }
            } else {
                Div { Text("Carregando...") }
            }

            fun fetchAndClean() {
                modalState = "closed"
                coroutineScope.launch { categoriesData = categories.getCategories() }
                cleanFormFields()
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
                                    val (status, message) = commonRepo.postRequest(
                                        "$apiCategoriesPath/update-category",
                                        CategoryItem(categoryId, categoryName, false),
                                        "put"
                                    )
                                    if (status == 201) alertTimer(message)
                                    fetchAndClean()
                                } else {
                                    // Save category --------->
                                    val (status, message) = commonRepo.postRequest(
                                        "$apiCategoriesPath/create-category",
                                        CategoryItem(null, categoryName, false)
                                    )
                                    if (status == 201) alertTimer(message)
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
                        categoryName = ""
                        categoryId = 0
                        fetchAndClean()
                    }
                }
            }
        }
    }
}
