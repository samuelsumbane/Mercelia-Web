package components

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import repository.Role

data class BtnDetails(
    val btnClass: String,
    val navTo: String,
    val btnText: String
)

@Composable
fun Menu(activePath: String, userRole: String) {

    val router = Router.current
    val btnsListClasses = when (userRole) {
        Role.V.desc -> {
            listOf(
                BtnDetails("sidebar-btn-sales", "/basicSellPage", "Vendas"),
                BtnDetails("sidebar-btn-products", "/basicProductsPage", "Productos"),
                BtnDetails("sidebar-btn-reports", "/basicReportsPage", "Inventários"),
            )
        }
        Role.G.desc, Role.A.desc -> {
            listOf(
                BtnDetails("sidebar-btn-home", "/dashboard", "Home"),
                BtnDetails("sidebar-btn-sales", "/basicSellPage", "Vendas"),
                BtnDetails("sidebar-btn-partners","/basicPartnersPage", "Parceiros"),
                BtnDetails("sidebar-btn-products", "/basicProductsPage", "Productos"),
                BtnDetails("sidebar-btn-reports", "/basicReportsPage", "Inventários"),
                BtnDetails("sidebar-btn-settings", "/basicSettingsPage", "Definições"),
            )
        }
        else -> { emptyList<BtnDetails>() }
    }

    var sideBarState by remember { mutableStateOf("-") }


    Div(attrs = { classes("sidebar-container") }) {
        Div(attrs = { classes("sidebar", "sidebar-item", sideBarState) }) {
            Div(attrs = { classes("sidebar-header-div") }) {
                Div(attrs = {
                    id("div-hamburger")
                    classes("sidebar-item")
                }) {
                    Button(attrs = {
                        classes("hamburger", "sidebar-item", sideBarState)
                        onClick {
                            sideBarState = if (sideBarState == "active") "-" else "active"
                        }
                    }) {
                        repeat(3){
                            Span(attrs = { classes("bar", "sidebar-item") })
                        }
                    }
                }
            }

            Div(attrs = { classes("sidebar-main-div") }) {
                for(btn in btnsListClasses) {
                    Button(attrs = {
                        if (btn.btnClass == activePath) {
                            classes("sidebar-btn", "tooltip", "active-btn", btn.btnClass)
                        } else {
                            classes("sidebar-btn", "tooltip", btn.btnClass)
                        }
                        onClick { router.navigate(btn.navTo) }
                    }) {
                        Text(btn.btnText)
                        Span(attrs = { classes("tooltiptext") }) {
                            Text(btn.btnText)
                        }
                    }
                }
            }

            Div(attrs = { classes("sidebar-footer-div") }) {
//                Button(
//                    attrs = {
//                        classes("l")
//                        onClick {
//                            router.navigate("/afiliatePage")
//                        }
//                    }
//                ) {
//                  Text("My p")
//                }
            }
        }
    }
}
