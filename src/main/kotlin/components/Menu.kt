package components

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import repository.Role
import repository.SysPackages

data class BtnDetails(
    val btnClass: String,
    val navTo: String,
    val btnText: String
)

@Composable
fun Menu(activePath: String, userRole: String, sysPackage: String) {

    val router = Router.current
    val activeSysPackage by remember { mutableStateOf(sysPackage) }

    val baseBtns = listOf(
        BtnDetails("sidebar-btn-home", "/dashboard", "Home"),
        BtnDetails("sidebar-btn-sales", "/sale-module", "Vendas"),
        BtnDetails("sidebar-btn-partners","/partners-module", "Parceiros"),
        BtnDetails("sidebar-btn-products", "/products-module", "Productos"),
        BtnDetails("sidebar-btn-reports", "/inventories-module", "Inventários"),
        BtnDetails("sidebar-btn-settings", "/basicSettingsPage", "Definições"),
    )


    val btnsListClasses = when (userRole) {
        Role.V.desc -> {
            listOf(
                BtnDetails("sidebar-btn-sales", "/sale-module", "Vendas"),
                BtnDetails("sidebar-btn-products", "/products-module", "Productos"),
                BtnDetails("sidebar-btn-reports", "/inventories-module", "Inventários"),
                BtnDetails("sidebar-btn-user", "/eachUser", "Perfil"),
            )
        }
        Role.G.desc, Role.A.desc -> {
            if (activeSysPackage == SysPackages.PO.desc) {
                baseBtns.toMutableList().apply {
                    add(4, BtnDetails("sidebar-btn-finance", "/finances-module", "Finanças"))
                }
            } else {
                baseBtns
            }
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
                        attr("aria-label", "Alternar menu lateral")
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

            Div(attrs = { classes("sidebar-footr-div") }) {

            }
        }
    }
}
