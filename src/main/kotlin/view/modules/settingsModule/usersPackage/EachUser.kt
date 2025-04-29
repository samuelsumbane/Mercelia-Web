package view.Afiliates

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
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

typealias AfMap = Map<String, String>


@Composable
fun eachUserPage(userId: Int, sysPackage: String) {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val users = UserRepository(httpClient)

    var data by remember { mutableStateOf(emptyUserItem) }
    var error by remember { mutableStateOf<String?>(null) }
    var email by remember { mutableStateOf("") }
    var minModalState by remember { mutableStateOf("closed") } //closed = "" --------->>
    var securityModalState by remember { mutableStateOf("closed") } //closed = "" --------->>
    val coroutineScope = rememberCoroutineScope()

    var submitBtnText by remember { mutableStateOf("Submeter") }
    var userName by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("") }
    var status by remember { mutableStateOf("") }
    var lastLogin by remember { mutableStateOf("") }
    var userNameError by remember { mutableStateOf("") }

    var userEmail by remember { mutableStateOf("") }
    var isLoggedIn by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        try {
            data = users.getUserById(userId).also {
                userName = it.name
                email = it.email
                role = it.role
                lastLogin = it.lastLogin
            }
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        }
    }

    val router = Router.current
    NormalPage(
        title = "Perfil", showBackButton = true,
        onBackFunc = { router.navigate("/") },
        pageActivePath = "sidebar-btn-user",
        sysPackage = sysPackage,
        userRole = role,
        navButtons = {}
    ) {

        Div {}
        //        console.log(data?.afAccountStatus)

        Div(attrs = { id("afiliateDivData") }) {
            H2 { Text("Dados do usuário") }

            // Personal data ------->>
            val personalData: AfMap = mapOf(
                "Nome" to data.name,
                "Email" to data.email
            )

            // Account data ------->>
            val afData: AfMap = mapOf(
                "Papel" to data.role,
                "Estado" to data.status,
                "Último login" to data.lastLogin,
            )

            // Security ------->>
            val afSec: AfMap = mapOf(
                "Senha" to "********",
            )

            H3 { Text("Dados pessoais") }
            personalData.forEach {
                pItem(it.key, it.value)
            }

            Div(attrs = { classes("div-item", "no-border") }) {
                P {}
                button("btn", "Editar dados pessoais") {
                    minModalState = "open-min-modal"
                    console.log("clicked")
                }
            }

            H3 { Text("Dados da conta") }
            afData.forEach {
                pItem(it.key, it.value)
            }

            H3 { Text("Segurança") }
            afSec.forEach {
                pItem(it.key, it.value)
            }
            Div(attrs = { classes("div-item", "no-border") }) {
                P {}
                button("btn", "Editar senha") {
                    securityModalState = "open-min-modal"
                    console.log("clicked")
                }
            }
        }

    }



    minModal(minModalState, "Editar Dados Pessoais") {
        Form(
            attrs = {
                classes("modalform")
                onSubmit { event ->
                    event.preventDefault()
                    userNameError = if (userName.isBlank()) "O nome é obrigatório" else ""
                    if (userNameError.isBlank()) {
                        coroutineScope.launch {
                            //                            val editAfiliateDraft = EditAfiliateDraft(
                            //                                userId, userName, afPhone, afLocation, "",
                            //                                afBirthday, afEmail
                            //                            )
                            //                            users.editAfiliate(editAfiliateDraft)
                            //                            alert("success", "Sucesso!", "Dados do afiliado actualizados com sucesso")
                        }
                    }

                }
            }
        ) {
            formDiv(
                "Nome", userName, InputType.Text,
                oninput = { event -> userName = event.value }, userNameError
            )

            formDiv(
                "Email", userEmail, InputType.Email,
                oninput = { event -> userEmail = event.value }, ""
            )

            Div(attrs = { classes("min-submit-buttons") }) {
                button("closeButton", "Fechar") {
                    minModalState = "closed"

                    coroutineScope.launch {
                    }
                }
                button("submitButton", submitBtnText, ButtonType.Submit)
            }
        }
    }

    minModal(securityModalState, "Editar e senha") {

        var afPasscode by remember { mutableStateOf("") }
        var afPasscodeError by remember { mutableStateOf("") }
        var afNewPassword by remember { mutableStateOf("") }
        var afNewPasswordError by remember { mutableStateOf("") }
        var afConfirmPassword by remember { mutableStateOf("") }
        var afConfirmPasswordError by remember { mutableStateOf("") }

        var passwordMatches by remember { mutableStateOf(false) }



        Form(
            attrs = {
                classes("modalform")
                onSubmit { event ->
                    event.preventDefault()
                    afPasscodeError = if (afPasscode.isBlank()) "A senha actual é obrigatória" else ""
                    afNewPassword = if (afNewPassword.isBlank()) "A nova é obrigatória" else ""
                    afConfirmPassword = if (afConfirmPassword.isBlank()) "É obrigatório confirmar a senha" else ""
                    afConfirmPassword =
                        if (afConfirmPassword != afNewPassword) "As senhas não correspondem." else ""



                    if (afPasscodeError.isBlank() && afNewPassword.isBlank() && afConfirmPassword.isBlank()) {
                        coroutineScope.launch {

                            //                            val afiliatePasswords = VerifyPasswordType(afPasscode, afNewPassword)
                            //                            passwordMatches = users.verifyPassword(afiliatePasswords)

                            if (!passwordMatches) {
                                afPasscodeError = "A senha inserida não corresponde a senha actual"
                            } else {
                                // Here we will edit password
                            }

                            val editAfiliateDraft = UserItemDraft(userName, userEmail, role)
                            users.updateUser(editAfiliateDraft)
                            alertTimer("Dados do afiliado actualizados com sucesso")
                        }
                    }
                }
            }
        ) {

            formDiv(
                "Senha actual", afPasscode, InputType.Password,
                oninput = { event -> afPasscode = event.value }, afPasscodeError
            )

            formDiv(
                "Nova senha", afNewPassword, InputType.Password,
                oninput = { event -> afNewPassword = event.value }, afNewPasswordError
            )

            formDiv(
                "Confirmar a senha", afConfirmPassword, InputType.Password,
                oninput = { event -> afConfirmPassword = event.value }, afConfirmPasswordError
            )

            Div(attrs = { classes("min-submit-buttons") }) {
                button("closeButton", "Fechar") {
                    securityModalState = "closed"
                }
                button("submitButton", submitBtnText, ButtonType.Submit)
            }
        }
    }
}

@Composable
fun pItem(pKey: String, pValue: String) {
    Div(attrs = {classes("div-item")}) {
        P { Text("$pKey: ") }
        P { Text(pValue) }
    }
}