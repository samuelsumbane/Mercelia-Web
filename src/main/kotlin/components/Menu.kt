package components

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import kotlinx.browser.sessionStorage
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Span
import org.jetbrains.compose.web.dom.Text
import repository.Role
import repository.SysPackages
import view.setThemeMode
import view.state.AppState.userName
import view.state.UiState.actualTheme
import view.state.UiState.currentActualThemeName
import view.state.UiState.showPerfilDiv
import view.state.UiState.showThemeModeChooserDiv

data class BtnDetails(
    val btnClass: String,
    val navTo: String,
    val btnText: String
)

@Composable
fun Menu(activePath: String, userRole: String, sysPackage: String) {

    val router = Router.current
    val coroutineScope = rememberCoroutineScope()
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
                BtnDetails("sidebar-btn-settings", "/basicSettingsPage", "Notificações"),
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
                    if (btn.navTo == "/eachUser") {
                        Div {
                            Button(attrs = {
                                if (btn.btnClass == activePath) {
                                    classes("sidebar-btn", "tooltip", "active-btn", btn.btnClass)
                                } else {
                                    classes("sidebar-btn", "tooltip", btn.btnClass)
                                }
                                onClick { showPerfilDiv = !showPerfilDiv }
                            }) {
                                Text(btn.btnText)
                                Span(attrs = { classes("tooltiptext") }) {
                                    Text(btn.btnText)
                                }
                            }
                            if (showPerfilDiv) {
                                UserPerfilOptions("sidebar-user-perfil", userName, currentActualThemeName, onChangeTheme = {
                                    showThemeModeChooserDiv = !showThemeModeChooserDiv
                                })
                            }
                            if (showThemeModeChooserDiv) {
                                OptionsDiv("themeModeOptions-sidebar") {
                                    OptionsDivItem("Auto", "Usa o mesmo tema do dispositivo") {
                                        setThemeMode("Auto")
                                        actualTheme = "Auto"
                                    }
                                    OptionsDivItem("Claro", "Fundo claro com texto escuro") {
                                        setThemeMode("Light")
                                        actualTheme = "Light"
                                    }

                                    OptionsDivItem("Escuro", "Fundo escuro com texto claro") {
                                        setThemeMode("Dark")
                                        actualTheme = "Dark"
                                    }
                                }
                            }
                        }
                    } else {
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
            }

            Div(attrs = { classes("sidebar-footr-div") }) {

            }
        }
    }
}
