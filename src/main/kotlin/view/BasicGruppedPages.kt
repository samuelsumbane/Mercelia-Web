package view

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import app.softwork.routingcompose.navigate
import components.BtnDetails
import components.Menu
import org.jetbrains.compose.web.dom.*

@Composable
fun basicGruppedPages(
    pageTitle: String,
    routeList: List<BtnDetails>,

) {
    val router = Router.current
    val btnsListClasses by remember { mutableStateOf(routeList) }

    if (btnsListClasses.size == 1) {
        router.navigate(btnsListClasses.first().navTo)
    } else {
        Menu(activePath = "sidebar-btn-management")
        Div(attrs = { classes("content-container")}) {
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

                Div(attrs = { classes("management-page-body")}) {

                    Div(attrs = { classes("management-page-body-main")}) {
                        Div(attrs = { classes("page-card-div")}) {
                            for(btn in btnsListClasses) {
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




}