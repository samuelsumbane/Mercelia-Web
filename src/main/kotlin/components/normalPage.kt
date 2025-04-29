package components

//import androidx.compose.runtime.Composable
import androidx.compose.runtime.*
import org.jetbrains.compose.web.dom.Button
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.H2
import org.jetbrains.compose.web.dom.Text


@Composable
fun NormalPage(
    title: String,
    showBackButton: Boolean = false,
    onBackFunc: () -> Unit = { console.log("onThis") },
    hasMain: Boolean = false,
    hasNavBar: Boolean = false,
    navButtons: @Composable () -> Unit = {},
    pageActivePath: String,
    sysPackage: String,
    userRole: String,
    content: @Composable () -> Unit
) {
    Menu(activePath = pageActivePath, userRole,  sysPackage)
    Div(attrs = { classes("content-container") }) {
        Div(attrs = { classes("normal-page") }) {
            Div(
                attrs = {
                    id("centerContainer")
                    classes("normal-page-header")
                }
            ) {
                Div(attrs = { classes("titleDiv") }) {
                    if (showBackButton)
                    button("backButton", "") {
                        onBackFunc()
                    }

                    H2 { Text(title) }

                }

                if (hasNavBar) {
                    Div(attrs = { classes("navDiv") }) { navButtons() }
                }
            }

            if (hasMain) {
                Div(attrs = { classes("normal-page-body-main")}) { content() }
            } else {
                Div(attrs = { classes("normal-page-body-table")}) { content() }
            }
        }
    }
}

@Composable
fun normalBranchPage(
    showBackButton: Boolean = false,
    onBackFunc: () -> Unit = { console.log("onThis") },
    hasMain: Boolean = false,
    hasNavBar: Boolean = false,
    sysPackage: String,
    userRole: String,
    titleDivScope: @Composable () -> Unit = {},
    topContent: @Composable () -> Unit = {},
    navButtons: @Composable () -> Unit = {},
    content: @Composable () -> Unit
) {
    Menu(activePath = "sidebar-btn-products", userRole, sysPackage)
    Div(attrs = { classes("content-container") }) {
        Div(attrs = { classes("normal-page") }) {
            Div(
                attrs = {
                    id("centerContainer")
                    classes("normal-page-header")
                }
            ) {
                Div(attrs = { classes("titleDiv") }) {
                    if (showBackButton) {
                        button("backButton", "") {
                            onBackFunc()
                        }
                    }

                    H2 { Text("Sucursais") }

                    titleDivScope()
                }

                if (hasNavBar) {
                    Div(attrs = { classes("navDiv") }) { navButtons() }
                }
            }

            if (hasMain) {
                Div(attrs = { classes("normal-page-body-flex-column") }) {
                    Div(attrs = { classes("normal-page-body-flex-column-top-part") }) {
                        topContent()
                    }
                    Div(attrs = { classes("normal-page-body-main")}) { content() }
                }
            } else {
                Div(attrs = { classes("normal-page-body-table")}) { content() }
            }
        }
    }
}

