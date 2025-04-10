package view.modules.settingsModule.usersPackage

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
import repository.UserItem
import repository.UserItemDraft
import repository.UserRepository

@Composable
fun UsersPage() {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val users = UserRepository(httpClient)
    var usersData by remember { mutableStateOf<List<UserItem>?>(null) }

    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var modalTitle by remember { mutableStateOf("") }
    var modalState by remember { mutableStateOf("closed") } //closed = "" --------->>
//    var modalState by remember { mutableStateOf("open-min-modal") } //closed = "" --------->>
    var userId by remember { mutableStateOf(0) }

    var userEmail by remember { mutableStateOf("") }
    var userEmailError by remember { mutableStateOf("") }

    var userName by remember { mutableStateOf("") }
    var userNameError by remember { mutableStateOf("") }

    var role by remember { mutableStateOf("") }
    var roleError by remember { mutableStateOf("") }

    var submitBtnText by remember { mutableStateOf("Submeter") }
    var showDialog by remember { mutableStateOf(true) }
    var isLoggedIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoggedIn = users.checkSession()

        if (isLoggedIn) {
            try {
                usersData = users.fetchUsers()
            } catch (e: Exception) {
                error = "Error: ${e.message}"
            }
        }
    }

    if (isLoggedIn) {

        NormalPage(
            title = "Usuários",
            pageActivePath = "sidebar-btn-settings",
            hasMain = true,
            hasNavBar = true,
            navButtons = {

                button("btnSolid", "+ Usuário") {
                    modalTitle = "Adicionar Usuário"
                    modalState = "open-min-modal"
                    submitBtnText = "Submeter"
                }
            }) {

            if (usersData != null) {
                usersData!!.forEach { item ->
                    cardWG(title = "", cardButtons = {
                        userCardButtons(
                            onEditButton = { submitBtnText = "Promover a" },
                            onSuspendButton = {
                                //                        modalState = "open-min-modal"

                            }
                        )
                    }) {
                        CardPitem("Nome: ", item.name)
                        CardPitem("Email: ", item.email)
                        CardPitem("Papel: ", item.role)
                        CardPitem("Estado: ", item.status)
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
                                    val (statusCode, statusText) = users.createUser(
                                        (UserItemDraft(userName, userEmail, role))
                                    )

                                    if (statusCode == 201) {
                                        alert("success", "Usuário adicionado", "A senha é: $statusText")
                                        userName = ""
                                        userEmail = ""
                                    }
                                }
                            }
                        }
                    }
                ) {

                    formDiv(
                        "Nome do usuário", userName, InputType.Text, { event -> userName = event.value }, userNameError
                    )

                    formDiv(
                        "Email", userEmail, InputType.Email, { event -> userEmail = event.value }, userEmailError
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
                    }
                }
            }
        }
    } else userNotLoggedScreen()
}
