package view.modules.settingsModule

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.*
import repository.*
import view.state.AppState.isLoading
import view.state.UiState.modalState
import view.state.UiState.modalTitle
import view.state.AppState.error
import view.state.UiState.description
import view.state.UiState.paymentForm


@Composable
fun notificationsPage(userRole: String, sysPackage: String) {

    val notifications = NotificationRepository()
    val commonRepo = CommonRepository()

    var allNotifications by mutableStateOf(listOf<NotificationItem>())

    val coroutineScope = rememberCoroutineScope()

    val router = Router.current

    LaunchedEffect(Unit) {
        try {
            val dataDeffered = async { notifications.allNotifications() }
            allNotifications = dataDeffered.await()
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
            onBackFunc = { router.navigate("/finances-module") },
            title = "Noficações", pageActivePath = "/basicSettingsPage",
            sysPackage = sysPackage,
            userRole = userRole) {
                if (error == null) {
                    if (allNotifications.isEmpty()) {
                        div(divClasses = listOf("centerDiv")) {
                            Text("Nenhum registro de contas pagas.")
                        }
                    } else {
                        Table(attrs = {
                            classes("display", "myTable")
                        }) {
                            Thead {
                                Tr {
                                    Th { Text("Titulo") }
                                    Th { Text("Mensagem") }
                                    Th { Text("Tipo") }
                                    Th { Text("Criado Em") }
                                    Th { Text("Estado") }
                                }
                            }
                            Tbody {
                                allNotifications.map {
                                    val read = if (it.read) "Lido" else "Não Lido"
                                    Tr {
                                        Td { Text(it.title) }
                                        Td { Text(it.message) }
                                        Td { Text(it.type) }
                                        Td { Text(it.createdAt) }
                                        Td { Text(read) }
                                        Td {
                                            if (it.read) {
                                                button("deleteButton", "") {
                                                    coroutineScope.launch {
                                                        commonRepo.deleteRequest("$apiNotificationsPath/delete/${it.id}")
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
}
