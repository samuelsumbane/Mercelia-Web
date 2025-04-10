package components

import androidx.compose.runtime.Composable
import org.jetbrains.compose.web.dom.*

//@Composable
//fun userNotLoggedScreen() {
//    Div() {
//        H3() {
//            Text("Usuário não logado.\n Faça o login para aceder a essa pagina")
//        }
//        A(href = "/") {
//            Text("Ir para pagina de login")
//        }
//    }
//}

@Composable
fun userNotLoggedScreen() {
    Div(attrs = {
        classes("user-not-logged-container")
    }) {
        H3(attrs = {
            classes("user-not-logged-title")
        }) {
            Text("Usuário não logado.")
        }
        P (attrs = {
            classes("user-not-logged-message")
        }) {
            Text("Faça o login para acessar esta página.")
        }
        A(href = "/", attrs = {
            classes("login-button")
        }) {
            Text("Fazer Login")
        }
    }
}

