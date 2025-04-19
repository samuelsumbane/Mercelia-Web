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
    basicAlertPage(
        "Usuário não logado.",
        "Faça o login para acessar esta página.",
        "/",
        "Fazer Login"
    )
}

@Composable
fun userHasNotAccessScreen(to: String = "sales") {
    basicAlertPage(
        "Acesso Restrito.",
        "Você não tem acesso a está pagina.",
        newPagePath = if (to != "sales") "/#/$to" else "/#/sales",
        "Ir para vendas"
    )
}

@Composable
fun pageNotFoundScreen() {
    Div(attrs = {
        classes("not-found-container")
    }) {
        H1(attrs = {
            classes("not-found-title")
        }) {
            Text("404")
        }
        P(attrs = {
            classes("not-found-message")
        }) {
            Text("Página não encontrada.")
        }
        A(href = "/", attrs = {
            classes("home-button")
        }) {
            Text("Voltar para a página inicial")
        }
    }
}

@Composable
fun basicAlertPage(title: String, message: String, newPagePath: String, btnText: String) {
    Div(attrs = {
        classes("user-not-logged-container")
    }) {
        H3(attrs = {
            classes("user-not-logged-title")
        }) {
            Text(title)
        }
        P (attrs = {
            classes("user-not-logged-message")
        }) {
            Text(message)
        }
        A(href = newPagePath, attrs = {
            classes("login-button")
        }) {
            Text(btnText)
        }
    }
}
