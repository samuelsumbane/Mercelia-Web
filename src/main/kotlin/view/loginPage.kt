package view

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.formDiv
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.dom.*
import repository.LoginRequest
import repository.UserRepository

@Composable
fun loginPage() {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val users = UserRepository(httpClient)
    var isLoggedIn by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        isLoggedIn = users.checkSession()
    }

    val router = Router.current
    var email by remember { mutableStateOf("admin@gmain") }
    var password by remember { mutableStateOf("1111") }
    var errorText by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    if (isLoggedIn) {
        router.navigate("/dashboard")
    } else {
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
                                val (status, userId) = users.login(loginRequest)

                                if (status) {
                                    val userData = users.getUserById(userId)
                                    if (userData.role == "Vendedor/Caixa") {
                                        router.navigate("/sales")
                                    } else {
                                        router.navigate("/dashboard")
                                        console.log("devia ter id")
                                    }
                                } else {
                                    errorText = "Usuário ou senha invalida"
                                }
                            }
                        }
                    }
                }) {
                    Br()
                    H2 { Text("Mercelia") }
                    Br()

                    formDiv("", email, InputType.Text, onInput = { event -> email = event.value
                    }, "")
                    formDiv("", password, InputType.Password, onInput = { event -> password = event.value
                    }, "")
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
                        }) { Text("") }
                    }
                    Br()
                }
            }
        }
    }
}