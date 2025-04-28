package view

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import app.softwork.routingcompose.navigate
import components.BtnDetails
import components.Menu
import components.userNotLoggedScreen
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.dom.*
import repository.Role
import repository.UserRepository
import repository.emptyLoggedUser


@Composable
fun basicGruppedPages(
    pageTitle: String,
    routeList: List<BtnDetails>,
) {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val router = Router.current
    val users = UserRepository(httpClient)
    var isLoggedIn by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf(emptyLoggedUser) }

    LaunchedEffect(Unit) {
        val session = users.checkSession()
        if (session != null) {
            if (session.isLogged) {
                isLoggedIn = true
                user = session
            } else isLoggedIn = false
        } else {
            console.log("session expired")
        }
    }



    if (isLoggedIn) {
        val btnsListClasses by remember { mutableStateOf(routeList) }

        if (btnsListClasses.size == 1) {
            router.navigate(btnsListClasses.first().navTo)
        } else {
            Menu(activePath = "sidebar-btn-management", user.userRole)
            Div(attrs = { classes("content-container") }) {
                Div(attrs = { classes("management-page") }) {
                    Div(
                        attrs = {
                            id("centerContainer")
                            classes("management-page-header")
                        }
                    ) {
                        Div(attrs = { classes("titleDiv") }) {
                            H2 { Text(pageTitle) }
                        }
                    }

                    Div(attrs = { classes("management-page-body") }) {

                        Div(attrs = { classes("management-page-body-main") }) {
                            Div(attrs = { classes("page-card-div") }) {
                                for (btn in btnsListClasses) {
                                    Button(attrs = {
                                        classes("page-card")
                                        onClick { router.navigate(btn.navTo) }
                                    }) {
                                        Text(btn.btnText)
                                    }
                                }
                            }
                        }
                    }
                }

            }
        }
    } else userNotLoggedScreen()

}