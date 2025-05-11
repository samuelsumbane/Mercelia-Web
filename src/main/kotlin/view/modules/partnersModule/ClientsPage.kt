package view.Afiliates


import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.ButtonType
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
fun clientsPage(userRole: String, sysPackage: String) {

    val clients = ClientRepository()
    val commonRepo = CommonRepository()
    var clientData by remember { mutableStateOf<List<ClientItem>?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var clientId by remember { mutableStateOf(0) }
    var clientName by remember { mutableStateOf("") }
    var clientNameError by remember { mutableStateOf("") }
    var clientPhone by remember { mutableStateOf("") }
    val router = Router.current

    LaunchedEffect(Unit) {
        if (userRole != Role.V.desc) {
            try {
                clientData = clients.getClients()
            } catch (e: Exception) {
                error = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
    if (isLoading) {
        loadingModal()
    } else {
        NormalPage(
            showBackButton = true,
            onBackFunc = { router.navigate("/partners-module") },
            title = "Clientes", pageActivePath = "sidebar-btn-partners",
            sysPackage = sysPackage,
            userRole = userRole, hasMain = true, hasNavBar = true, navButtons = {
                button("btnSolid", "+ Cliente") {
                    modalTitle = "Adicionar Cliente"
                    modalState = "open-min-modal"
                    submitBtnText = "Submeter"
                    //
                    clientNameError = ""
                }
            }) {

            if (clientData != null) {
                if (clientData!!.isEmpty()) {
                    Div(attrs = { classes("centerDiv") }) {
                        Text("Nenhum cliente encontrado.")
                    }
                }
                clientData!!.forEach { item ->
                    cardWG(title = "", cardButtons = {
                        cardButtons(
                            onEditButton = {
                                modalTitle = "Editar cliente"
                                clientId = item.id!!
                                clientName = item.name
                                clientPhone = item.telephone
                                modalState = "open-min-modal"
                                submitBtnText = "Editar"
                            },
                            onDeleteButton = {
                                alertDelete("Tem certeza?", "Essa acção não pode ser desfeita") {
                                    coroutineScope.launch {
                                        val (status, message) = commonRepo.deleteRequest("$apiClientsPath/delete/$clientId")
                                        when (status) {
                                            200 -> {
                                                alertTimer(message)
                                                clientData = clients.getClients()
                                            }

                                            404 -> alert("error", "Cliente não encontrado.", message)
                                            406 -> alert("warning", "Delete não aceite.", message)
                                        }
                                    }
                                }
                            }
                        )
                    }) {
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
                                        val (status, message) = commonRepo.postRequest(
                                            "$apiClientsPath/edit",
                                            ClientItem(clientId, clientName, clientPhone),
                                            "put"
                                        )
                                        if (status == 201) alertTimer("Cliente actualizado com sucesso.")
                                        modalState = "closed"
                                        coroutineScope.launch { clientData = clients.getClients() }
                                    } else {
                                        val (status, message) = commonRepo.postRequest(
                                            "$apiClientsPath/create",
                                            ClientItem(null, clientName, clientPhone)
                                        )
                                        if (status == 201) alertTimer("Cliente adicionado com sucesso.")
                                    }
                                    clientId = 0
                                    clientName = ""
                                    clientPhone = ""
                                }
                            }

                        }
                    }
                ) {

                    formDiv(
                        "Nome", clientName, InputType.Text, 48,
                        { event -> clientName = event.value }, clientNameError
                    )

                    formDiv(
                        "Telefone", clientPhone, InputType.Text, 18,
                        { event -> clientPhone = event.value }, ""
                    )

                    Div(attrs = { classes("min-submit-buttons") }) {
                        button("closeButton", "Fechar") {
                            modalState = "closed"
                            coroutineScope.launch { clientData = clients.getClients() }
                        }
                        button("submitButton", btnText = submitBtnText, ButtonType.Submit)
                    }
                }
            }
        }
    }

}

