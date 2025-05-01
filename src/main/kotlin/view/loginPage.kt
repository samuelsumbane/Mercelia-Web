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
import repository.Role
import repository.UserRepository

@Composable
fun loginPage() {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val users = UserRepository(httpClient)

    // admin@gmain : 1111 -> admin
    // sam@gmail.com : 8262 -> gerente
    // seller@gmail : 5692  -> vendedor

    val router = Router.current
    var email by remember { mutableStateOf("admin@gmain") }
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
                                        if (userRole == Role.V.desc) {
                                            router.navigate("/sales")
                                        } else {
                                            router.navigate("/dashboard")
                                        }
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
                    H2 { Text("Mercelia") }
                    Br()

                    formDiv("", email, InputType.Text, oninput = { event -> email = event.value
                    }, "")
                    formDiv("", password, InputType.Password, oninput = { event -> password = event.value
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
                        }) { Text(errorText) }
                    }
                    Br()
                }
            }
        }

}