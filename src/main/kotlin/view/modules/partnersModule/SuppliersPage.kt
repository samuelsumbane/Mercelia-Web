package view.Afiliates


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
fun suppliersPage() {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val suppliers = SupplierRepository(httpClient)
    var supplierData by remember { mutableStateOf<List<SupplierItem>?>(null) }

    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var modalTitle by remember { mutableStateOf("") }
    var modalState by remember { mutableStateOf("closed") } //closed = "" --------->>
//    var modalState by remember { mutableStateOf("open-min-modal") } //closed = "" --------->>

    var supplierId by remember { mutableStateOf(0) }
    var supplierName by remember { mutableStateOf("") }
    var supplierNameError by remember { mutableStateOf("") }
    var supplierPhone by remember { mutableStateOf("") }
    var supplierAddress by remember { mutableStateOf("") }

    var submitBtnText by remember { mutableStateOf("Submeter") }

    NormalPage(title = "Fornecedores", pageActivePath = "sidebar-btn-partners", hasMain = true, hasNavBar = true, navButtons = {
        button("btnSolid", "+ Fornecedor") {
            modalTitle = "Adicionar Fornecedor"
            modalState = "open-min-modal"
            submitBtnText = "Submeter"
        }
    }) {
        LaunchedEffect(Unit) {
            try {
                supplierData = suppliers.getSuppliers()
            } catch (e: Exception) {
                error = "Error: ${e.message}"
            }
        }

        if (supplierData != null) {
            if (supplierData!!.isEmpty()) {
                Div(attrs = { classes("centerDiv") }) {
                    Text("Nenhum fornecedor encontrado.")
                }
            }
            supplierData!!.forEach { item ->
                cardWG(title = "", cardButtons = { cardButtons(
                    onEditButton = {
                        supplierId = item.id!!
                        supplierName = item.name
                        supplierPhone = item.contact
                        supplierAddress = item.address
                        modalState = "open-min-modal"
                        submitBtnText = "Editar"
                    },
                    showDeleteBtn = false
                ) }) {
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
                                    val status = suppliers.editSupplier(SupplierItem(supplierId, supplierName, supplierPhone, supplierAddress))
                                    if (status == 201) alert("success", "Sucesso!", "Fornecedor actualiado com sucesso.")
                                    else unknownErrorAlert()
                                } else {
                                    val status = suppliers.createSupplier(SupplierItem(null, supplierName, supplierPhone, supplierAddress))
                                    if (status == 201) alert("success", "Sucesso!", "Fornecedor adicionado com sucesso.")
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

                formDiv("Nome", supplierName, InputType.Text,
                    { event -> supplierName = event.value}, supplierNameError
                )

                formDiv("Telefone", supplierPhone, InputType.Text,
                    { event -> supplierPhone = event.value}, ""
                )
                
                formDiv("Endereço", supplierAddress, InputType.Text,
                    { event -> supplierAddress = event.value}, ""
                )

                submitButtons(submitBtnText) {
                    modalState = "closed"
                    coroutineScope.launch {
                        supplierData = suppliers.getSuppliers()
                    }
                }
            }
        }
    }
}

