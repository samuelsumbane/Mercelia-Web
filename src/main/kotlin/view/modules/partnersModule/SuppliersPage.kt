package view.Afiliates


import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
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
import view.state.AppState
import view.state.AppState.isLoading
import view.state.UiState.modalState
import view.state.AppState.error
import view.state.UiState.modalTitle


@Composable
fun suppliersPage(userRole: String, sysPackage: String) {

    val suppliers = SupplierRepository()
    val commonRepo = CommonRepository()
    var supplierData by remember { mutableStateOf<List<SupplierItem>?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var supplierId by remember { mutableStateOf(0) }
    var supplierName by remember { mutableStateOf("") }
    var supplierNameError by remember { mutableStateOf("") }
    var supplierPhone by remember { mutableStateOf("") }
    var supplierAddress by remember { mutableStateOf("") }
    var submitBtnText by remember { mutableStateOf("Submeter") }
    val router = Router.current

    fun closeModalAndFetch() {
        modalState = "closed"
        coroutineScope.launch {
            supplierData = suppliers.getSuppliers()
        }
    }

    LaunchedEffect(Unit) {
        try {
            supplierData = suppliers.getSuppliers()
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        } finally {
            isLoading = false
        }
    }
    if (AppState.isLoading) {
        loadingModal()
    } else {
        NormalPage(
            showBackButton = true,
            onBackFunc = { router.navigate("/partners-module") },
            title = "Fornecedores",
            pageActivePath = "sidebar-btn-partners",
            sysPackage = sysPackage,
            userRole = userRole,
            hasMain = true,
            hasNavBar = true,
            navButtons = {
                button("btnSolid", "+ Fornecedor") {
                    modalTitle = "Adicionar Fornecedor"
                    modalState = "open-min-modal"
                    submitBtnText = "Submeter"
                }
            }) {

            if (supplierData != null) {
                if (supplierData!!.isEmpty()) {
                    Div(attrs = { classes("centerDiv") }) {
                        Text("Nenhum fornecedor encontrado.")
                    }
                }
                supplierData!!.forEach { item ->
                    cardWG(title = "", cardButtons = {
                        cardButtons(
                            onEditButton = {
                                supplierId = item.id!!
                                supplierName = item.name
                                supplierPhone = item.contact
                                supplierAddress = item.address
                                modalState = "open-min-modal"
                                submitBtnText = "Editar"
                            },
                            showDeleteBtn = false
                        )
                    }) {
                        CardPitem("Nome", item.name)
                        CardPitem("Telefone", item.contact)
                        CardPitem("Endereço", item.address)
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

                            supplierNameError = if (supplierName.isBlank()) "O nome é obrigatório" else ""

                            if (supplierName.isNotBlank()) {
                                coroutineScope.launch {
                                    if (supplierId != 0) {
                                        val (status, message) = commonRepo.postRequest(
                                            "$apiSupplierPath/edit-supplier",
                                            SupplierItem(
                                                supplierId, supplierName, supplierPhone, supplierAddress
                                            ), "put"
                                        )
                                        if (status == 201) {
                                            alertTimer("Fornecedor actualiado com sucesso.")
                                            closeModalAndFetch()
                                        } else unknownErrorAlert()
                                    } else {
                                        val (status, message) = commonRepo.postRequest(
                                            "$apiSupplierPath/create-supplier",
                                            SupplierItem(
                                                null, supplierName, supplierPhone, supplierAddress
                                            )
                                        )
                                        if (status == 201) alertTimer("Fornecedor adicionado com sucesso.")
                                        else unknownErrorAlert()
                                    }

                                    supplierName = ""
                                    supplierPhone = ""
                                    supplierAddress = ""
                                }
                            }

                        }
                    }
                ) {

                    formDiv(
                        "Nome", supplierName, InputType.Text, 48,
                        { event -> supplierName = event.value }, supplierNameError
                    )

                    formDiv(
                        "Telefone", supplierPhone, InputType.Text, 48,
                        { event -> supplierPhone = event.value }, ""
                    )

                    formDiv(
                        "Endereço", supplierAddress, InputType.Text, 98,
                        { event -> supplierAddress = event.value }, ""
                    )

                    submitButtons(submitBtnText) { closeModalAndFetch() }
                }
            }
        }
    }

}

