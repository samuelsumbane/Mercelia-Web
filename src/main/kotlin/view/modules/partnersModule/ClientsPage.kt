package view.Afiliates


import androidx.compose.runtime.*
import components.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.dom.*
import repository.*



@Composable
fun clientsPage() {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val clients = ClientRepository(httpClient)
    var clientData by remember { mutableStateOf<List<ClientItem>?>(null) }

    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var modalTitle by remember { mutableStateOf("") }
    var modalState by remember { mutableStateOf("closed") } //closed = "" --------->>
//    var modalState by remember { mutableStateOf("open-min-modal") } //closed = "" --------->>

    var clientId by remember { mutableStateOf(0) }
    var clientName by remember { mutableStateOf("") }
    var clientNameError by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }

    var submitBtnText by remember { mutableStateOf("Submeter") }

    NormalPage(title = "Clientes", pageActivePath = "sidebar-btn-partners", hasMain = true, hasNavBar = true, navButtons = {
        button("btnSolid", "+ Cliente") {
            modalTitle = "Adicionar Cliente"
            modalState = "open-min-modal"
            submitBtnText = "Submeter"
        }
    }) {
        LaunchedEffect(Unit) {
            try {
                clientData = clients.getClients()
            } catch (e: Exception) {
                error = "Error: ${e.message}"
            }
        }

        if (clientData != null) {
            if (clientData!!.isEmpty()) {
                Div(attrs = { classes("centerDiv") }) {
                    Text("Nenhum cliente encontrado.")
                }
            }
            clientData!!.forEach { item ->
                cardWG(title = "", cardButtons = { cardButtons(
                    onEditButton = {
                        modalTitle = "Editar cliente"
                        clientId = item.id!!
                        clientName = item.name
                        clientPhone = item.telephone
                        modalState = "open-min-modal"
                        submitBtnText = "Editar"
                    },
                    showDeleteBtn = false
                ) }) {
                    CardPitem("Nome", item.name)
                    CardPitem("Telefone", item.telephone)
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

                        clientNameError = if (clientName.isBlank()) "O nome é obrigatório" else ""


                        if (clientName.isNotBlank()) {
                            coroutineScope.launch {
                                if (clientId != 0) {
                                    val status = clients.editClient(ClientItem(clientId, clientName, clientPhone))
                                    if (status == 201) alert("success", "Sucesso!", "Cliente actualizado com sucesso.")
                                    modalState = "closed"
                                } else {
                                    val status = clients.createClient(ClientItem(null, clientName, clientPhone))
                                    if (status == 201) alert("success", "Sucesso!", "Cliente adicionado com sucesso.")
                                }
                                clientId = 0
                                clientName = ""
                                clientPhone = ""
                            }
                        }

                    }
                }
            ) {

                formDiv("Nome", clientName, InputType.Text,
                    { event -> clientName = event.value}, clientNameError
                )

                formDiv("Telefone", clientPhone, InputType.Text,
                    { event -> clientPhone = event.value}, ""
                )

                Div(attrs = { classes("min-submit-buttons") }) {
                    button("closeButton", "Fechar") {
                        modalState = "closed"
                            coroutineScope.launch {
                            clientData = clients.getClients()
                        }
                    }
                    button("submitButton", btnText = submitBtnText, ButtonType.Submit)
                }
            }
        }
    }
}

