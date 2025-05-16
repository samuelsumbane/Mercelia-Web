package view.modules.settingsModule

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.css.DisplayStyle
import org.jetbrains.compose.web.css.FlexDirection
import org.jetbrains.compose.web.css.display
import org.jetbrains.compose.web.css.flexDirection
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.*
import repository.*
import view.state.AppState.allNotifications
import view.state.AppState.isLoading
import view.state.UiState.modalState
import view.state.UiState.modalTitle
import view.state.AppState.error
import view.state.UiState.description
import view.state.UiState.paymentForm
import view.state.UiState.submitBtnText


@Composable
fun notificationsPage(userRole: String, userId: Int, sysPackage: String) {

    val notifications = NotificationRepository()
    val commonRepo = CommonRepository()
    var notId by remember { mutableStateOf(0) }
    var notUser by remember { mutableStateOf("") }
    var notTtile by remember { mutableStateOf("") }
    var notMessage by remember { mutableStateOf("") }
    var notRead by remember { mutableStateOf("") }
    var notCreated by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val router = Router.current

    LaunchedEffect(Unit) {
        try {
            val dataDeffered = async { notifications.allNotifications() }
            allNotifications = if (userRole == Role.V.desc) dataDeffered.await().filter { it.userId == userId } else dataDeffered.await()
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        } finally {
            initializeDataTable()
            isLoading = false
        }
    }
    if (isLoading) {
        loadingModal()
    } else {
        NormalPage(
            showBackButton = true,
            onBackFunc = { router.navigate("/basicSettingsPage") },
            title = "Noficações", pageActivePath = "/basicSettingsPage",
            sysPackage = sysPackage,
            userRole = userRole) {
                if (error == null) {
                    if (allNotifications.isEmpty()) {
                        div(divClasses = listOf("centerDiv")) {
                            Text("Nenhuma notificação encontrada.")
                        }
                    } else {
                        Table(attrs = {
                            classes("display", "myTable")
                        }) {
                            Thead {
                                Tr {
                                    Th { Text("Para") }
                                    Th { Text("Titulo") }
                                    Th { Text("Mensagem") }
                                    Th { Text("Tipo") }
                                    Th { Text("Criado Em") }
                                    Th { Text("Estado") }
                                    Th { Text("Acções") }
                                }
                            }
                            Tbody {
                                allNotifications.map {
                                    val read = if (it.read) "Lida" else "Não Lida"
                                    Tr {
                                        Td { Text("${it.userName ?: "Todos"} ") }
                                        Td { Text(it.title.cut(12)) }
                                        Td { Text(it.message.cut(25)) }
                                        Td { Text(it.type) }
                                        Td { Text(it.createdAt) }
                                        Td { Text(read) }
                                        Td {
                                            button("smallDeleteBtn", "", hoverText = "Deletar") {
                                                alertDelete("Deletar está categoria?", "Está acção não pode ser desfeita.") {
                                                    coroutineScope.launch {
                                                        val (status, message) = commonRepo.deleteRequest("$apiNotificationsPath/delete/${it.id}")
                                                        when (status) {
                                                            200 -> {
                                                                alertTimer(message)
                                                                allNotifications = notifications.allNotifications()
                                                            }
                                                            406 -> alert("warning", "O delete falhou", message)
                                                            else -> unknownErrorAlert()
                                                        }
                                                    }
                                                }

                                            }
                                            button("smallEyeBtn", "", hoverText = "Ver") {
                                                coroutineScope.launch {
                                                    notId = it.id
                                                    fun fillVals() {
                                                        notUser = it.userName ?: ""
                                                        notTtile = it.title
                                                        notMessage = it.message
                                                        notCreated = it.createdAt
                                                        modalState = "open-min-modal"
                                                        modalTitle = "Detalhes da notificação"
                                                    }
                                                    if (!it.read) {
                                                        val (status, _) = commonRepo.postRequest("$apiNotificationsPath/update", IdAndReadState(notId, true), "put")
                                                        if (status == 201) {
                                                            notRead = "Lida"
                                                            fillVals()
                                                        }
                                                    } else {
                                                        notRead = read
                                                        fillVals()
                                                    }

                                                }

                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                } else if (error != null) {
                    Div { Text(error!!) }
                } else {
                    Div { Text("Loading...") }
                }
            }
        }
    // More product details --------->>
    minModal(modalState, modalTitle) {
        Form(attrs = {
            classes("modalform")
        }) {

            if (notUser.isNotEmpty()) {
                modalPItem("Usuário: ", value = {
                    P { Text(notUser) }
                })
            }

            modalPItem("Titulo: ", value = {
                P { Text(notTtile) }
            })
            Br()
            Hr()
            modalPItem("Mensagem: ", value = {
                P { Text(notMessage) }
            })
            Br()
            Hr()
            modalPItem("Estado: ", value = {
                P { Text(notRead) }
            })
            modalPItem("", value = {
                button("btn", "Marcar como não lida") {
                    coroutineScope.launch {
                        val (status, message) = commonRepo.postRequest("$apiNotificationsPath/update", IdAndReadState(notId, false), "put")
                        if (status == 201) {
                            alertTimer(message)
                        } else unknownErrorAlert()
                    }
                }
            })
            Br()
            Hr()
            modalPItem("Criado em: ", value = {
                P { Text(notCreated) }
            })

            Br()
            Hr()
            Div(attrs = {
                classes("min-submit-buttons")
            }) {
                button("closeButton", "Fechar") {
                    coroutineScope.launch {
                        allNotifications = notifications.allNotifications()
                    }
                    modalState = "closed"
                    modalTitle = "Detalhes da notificação"
                    notId = 0
                    notUser = ""
                    notTtile = ""
                    notMessage = ""
                    notRead = ""
                    notCreated = ""
                }
            }


        }
    }


}
