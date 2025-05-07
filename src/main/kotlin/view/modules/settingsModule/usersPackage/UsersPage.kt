package view.modules.settingsModule.usersPackage

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
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import repository.*

@Composable
fun UsersPage(userRole: String, sysPackage: String) {

    val users = UserRepository()
    val commonRepo = CommonRepository()
    var usersData by remember { mutableStateOf<List<UserItem>?>(null) }
    var sysPackage by remember { mutableStateOf(sysPackage) }

    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var modalTitle by remember { mutableStateOf("") }
    var modalState by remember { mutableStateOf("closed") } //closed = "" --------->>
    var moreDetailsModalState by remember { mutableStateOf("closed") } //closed = "" --------->>
//    var modalState by remember { mutableStateOf("open-min-modal") } //closed = "" --------->>
    var userId by remember { mutableStateOf(0) }

    var userEmail by remember { mutableStateOf("") }
    var userEmailError by remember { mutableStateOf("") }

    var userName by remember { mutableStateOf("") }
    var userNameError by remember { mutableStateOf("") }

    var userStatus by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var newRole by remember { mutableStateOf("") }
    var roleError by remember { mutableStateOf("") }
    val router = Router.current
    var submitBtnText by remember { mutableStateOf("Submeter") }

    LaunchedEffect(Unit) {
        try {
            usersData = users.fetchUsers()
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        }
    }


    fun alertStatusAndMessageResponse(status: Int, message: String) {
        when (status) {
            200 -> {
                alertTimer(message)
                coroutineScope.launch { usersData = users.fetchUsers() }
            }
            404 -> alert("error", "Usuário não encontrado", message)
            else -> unknownErrorAlert()
        }
    }

    fun cleanFields() {
        userId = 0
        userName = ""
        userEmail = ""
    }

    NormalPage(
        showBackButton = true,
        onBackFunc = { router.navigate("/basicSettingsPage") },
        title = "Usuários",
        pageActivePath = "sidebar-btn-settings",
        sysPackage = sysPackage,
        hasMain = true,
        hasNavBar = true,
        userRole = userRole,
        navButtons = {
            if (usersData != null) {
                val canAddUser = (sysPackage == SysPackages.L.desc && usersData!!.size < 3) ||
                        (sysPackage == SysPackages.PL.desc && usersData!!.size < 10) ||
                        (sysPackage == SysPackages.PO.desc)
                if (canAddUser) {
                    button("btnSolid", "+ Usuário") {
                        modalTitle = "Adicionar Usuário"
                        modalState = "open-min-modal"
                        submitBtnText = "Submeter"
                    }
                }
            }

        }) {

        if (usersData != null) {
            usersData!!.forEach { item ->
                cardWG(title = "", cardButtons = {
                    userCardButtons(
                        onSeeMoreDetailsButton = {
                            userName = item.name
                            userEmail = item.email
                            role = item.role
                            userStatus = item.status
                            userId = item.id
                            moreDetailsModalState = "open-min-modal"
                        },
                    )
                }) {
                    CardPitem("Nome ", item.name)
                    CardPitem("Email ", item.email)
                    CardPitem("Papel ", item.role)
                    CardPitem("Estado ", item.status)
                }
            }
        } else if (error != null) {
            Div { Text(error!!) }
        } else {
            Div { Text("Loading...") }
        }

        minModal(modalState, modalTitle) {
            Form(
                attrs = {
                    classes("modalform")
                    onSubmit { event ->
                        event.preventDefault()

                        userNameError = if (userName == "") "O nome do usuário é obrigatório." else ""
                        userEmailError = if (userEmail == "") "O email é obrigatório." else ""

                        if (userName != "") {
                            coroutineScope.launch {
                                val (statusCode, message) = commonRepo.postRequest("$apiPath/user/create_user",
                                    (UserItemDraft(userName, userEmail, role))
                                )

                                when (statusCode) {
                                    101, 102, 103 -> alert("error", "Usuário não adicionado", message)
                                    201 -> alert("success", "Usuário adicionado com sucesso", "A senha é: $message")
                                    404 -> alert("error", "Usuário não adicionado", message)
                                    else -> unknownErrorAlert()
                                }
                                userName = ""
                                userEmail = ""
                            }
                        }
                    }
                }
            ) {

                formDiv(
                    "Nome do usuário",
                    userName,
                    InputType.Text, 48,
                    { event -> userName = event.value },
                    userNameError
                )

                formDiv(
                    "Email", userEmail, InputType.Email, 100, { event -> userEmail = event.value }, userEmailError
                )

                Div {
                    Label { Text("Papel") }
                    Select(attrs = {
                        classes("formTextInput")
                        onChange {
                            it.value?.let { option ->
                                role = option.split(" - ")[1]
                            }
                        }
                    }) {
                        Option("0 - Vendedor/Caixa") {
                            Text("Vendedor/Caixa")
                        }
                        Option("1 - Gerente") {
                            Text("Gerente")
                        }
                        Option("2 - Administrador") {
                            Text("Administrador")
                        }
                        Option("3 - Estoquista") {
                            Text("Estoquista")
                        }
                        if (role.isBlank()) role = "Vendedor/Caixa"
                    }
                }

                submitButtons {
                    coroutineScope.launch {
                        usersData = users.fetchUsers()
                    }
                    modalState = "u"
                    cleanFields()
                }
                Br()
            }
        }

        minModal(moreDetailsModalState, "Detalhes do usuário") {
            Form(
                attrs = { classes("modalform") }
            ) {
                modalPItem("Nome do usuário", value = {
                    P { Text(userName) }
                })
                modalPItem("Email", value = {
                    P { Text(userEmail) }
                })
                modalPItem("Estado", value = {
                    P { Text(userStatus) }
                })

                modalPItem("", value = {
                    if (userStatus == "Activo") {
                        button("btn", "Bloquear") {
                            coroutineScope.launch {
                                val (status, message) = commonRepo.postRequest("$apiPath/user/change-status",
                                    ChangeStatusDC(2, userId)
                                )
                                alertStatusAndMessageResponse(status, message)
                            }
                        }
                    } else {
                        button("btn", "Activar") {
                            coroutineScope.launch {
                                val (status, message) = commonRepo.postRequest("$apiPath/user/change-status",
                                    ChangeStatusDC(1, userId)
                                )
                                alertStatusAndMessageResponse(status, message)
                            }
                        }
                    }

                })
                Br()
                Hr()
                Br()
//                modalPItem("Histórico de vendas", value = {
//                })

                modalPItem("Papel", value = {
                    P { Text(role) }
                })

                modalPItem("Pro/Despromover à", value = {
                    Div(attrs = {
                        style {
                            display(DisplayStyle.Flex)
                            flexDirection(FlexDirection.Column)
                        }
                    }) {
                        Select(attrs = {
                            classes("formTextInput")
                            onChange {
                                it.value?.let { option ->
                                    newRole = if (option.split(" - ")[0] != "0") {
                                        option.split(" - ")[1]
                                    } else ""
                                }
                            }
                        }) {
                            Option("0 - Novo papel") {
                                Text("Novo Papel")
                            }
                            Option("1 - Vendedor/Caixa") {
                                Text("Vendedor/Caixa")
                            }
                            Option("2 - Gerente") {
                                Text("Gerente")
                            }
                            Option("3 - Administrador") {
                                Text("Administrador")
                            }
                            Option("4 - Estoquista") {
                                Text("Estoquista")
                            }
                        }
                    }

                    if (newRole.isNotBlank()) {
                        button("checkButton", "") {
                            coroutineScope.launch {
                                val (status, message) = commonRepo.postRequest("$apiPath/user/change-role",
                                    ChangeRoleDC(newRole, userId)
                                )
                                alertStatusAndMessageResponse(status, message)
                                newRole = ""
                            }
                        }
                    }
                })
                Br()
                Hr()
                Br()

                Div(attrs = {
                    classes("min-submit-buttons")
                }) {
                    button("closeButton", "Fechar") { moreDetailsModalState = "closed" }
                }
            }
        }
    }
}
