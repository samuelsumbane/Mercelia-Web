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
    userRole: String,
    sysPackage: String,
    activePage: String,
) {

    val router = Router.current
    val btnsListClasses by remember { mutableStateOf(routeList) }

    if (btnsListClasses.size == 1) {
        router.navigate(btnsListClasses.first().navTo)
    } else {
        Menu(activePath = activePage, userRole, sysPackage)
//        Div(attrs = { classes("content-container") }) {
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

                    Div(attrs = { classes("management-page-body-card-div") }) {
                        for (btn in btnsListClasses) {
                            Button(attrs = {
                                classes("management-page-body-card-div-btn")
                                onClick { router.navigate(btn.navTo) }
                            }) {
                                Text(btn.btnText)
                            }
                        }
                    }
                }
            }
//        }
    }
}