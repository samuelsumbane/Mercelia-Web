package view

import androidx.compose.runtime.*
import androidx.compose.runtime.setValue
import app.softwork.routingcompose.Router
import components.formDiv
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.dom.*
import repository.LoginRequest
import repository.Role
import repository.UserRepository
import view.state.UiState.actualTheme
import view.state.UiState.currentActualThemeName

@Composable
fun loginPage() {

    val users = UserRepository()
    val router = Router.current

    actualTheme = localStorage.getItem("system_theme") ?: run {
        localStorage.setItem("system_theme", "Auto")
        "Auto".also { actualTheme = it }
    }
    currentActualThemeName =
        when (actualTheme) {
            "Light" -> "Claro"
            "Dark" -> "Escuro"
            else -> "Auto"
        }


    sessionStorage.getItem("reloadFromLogin")?.let {
        when (it) {
           "1" -> router.navigate("/dashboard")
           "2" -> router.navigate("/sales")
        }
        sessionStorage.removeItem("reloadFromLogin")
    }


    // admin@gmain : 1110 -> admin
    // sam@gmail.com : 8262 -> gerente
    // seller@gmail :   -> vendedor
    // marcos@gmail: 3792 -> 1111 -> vendedor

    var email by remember { mutableStateOf("admin@gmail") }
    var password by remember { mutableStateOf("1110") }
    var errorText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

        Div(attrs = { classes("login-page")}) {
            Div(attrs = { classes("loginDiv")}) {
                Form(attrs = {
                    id("loginForm")
                    onSubmit { event ->
                        event.preventDefault()
                        errorText = if (email == "" || password == "") "Por favor, preencha todos os campos" else ""
                        if (errorText.isBlank()) {
                            coroutineScope.launch {
                                val loginRequest = LoginRequest(email,password)
                                val (status, userRole) = users.login(loginRequest)

                                if (status) {
                                    if (userRole.isNotBlank()) {
                                        val userIdentifier = if (userRole == Role.V.desc) "2" else "1"
                                        sessionStorage.setItem("reloadFromLogin", userIdentifier)
                                        window.location.reload()
                                    } else {
                                        errorText = "Usuário ou senha invalida"
                                        console.error("Login failed.")
                                    }
                                } else {
                                    errorText = "Usuário ou senha invalida"
                                }
                            }
                        }
                    }
                }) {
                    Br()
                    H2 { Text("SSPT") }
                    Br()

                    formDiv(label = "", inputValue = email, inputType = InputType.Text, oninput = { event -> email = event.value
                    }, spanError = "")
                    formDiv(label = "", inputValue = password, inputType = InputType.Password, oninput = { event -> password = event.value
                    }, spanError = "")
                    Br()
                    Div(attrs = { classes("loginbutton-div")}) {
                        Button(attrs = {
                            classes("login-button")
                            ButtonType.Submit
                        }) {
                            Text("Submit")
                        }
                    }

                    Div(attrs = { classes("login_info")}) {
                        P(attrs = {
                            classes("errorText")
                            id("error_mensage")
                        }) { Text(errorText) }
                    }
                    Br()
                }
            }
        }

}