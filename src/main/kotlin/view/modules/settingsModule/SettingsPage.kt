import androidx.compose.runtime.*
import components.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.readOnly
import org.jetbrains.compose.web.dom.*
import repository.*


data class ConfigDetailsDc( // Dc (Data class) ------->>
    val title: String,
    val description: String,
    var value: String,
    val readOnly: Boolean = false
)



@Composable
fun settingsPage() {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val settings = SettingsRepository(httpClient)
    val users = UserRepository(httpClient)
    var sysConfigs by remember { mutableStateOf(emptyList<SysConfigItem>()) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf(emptyLoggedUser) }
    var activeSysPackage by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val session = users.checkSession()
        if (session != null) {
            if (session.isLogged) {
                isLoggedIn = true
                user = session
            } else {
                isLoggedIn = false
            }
        } else {
            console.log("session expired")
        }

        if (isLoggedIn) {
            sysConfigs = settings.getSettings()
        }
    }

    if (isLoggedIn) {
        if (user.userRole != Role.A.desc) {
            userHasNotAccessScreen("dashboard")
        } else {
            Menu(activePath = "sidebar-btn-settings", user.userRole)

            Div(attrs = { classes("content-container", "def-page") }) {
                Div(attrs = { classes("def-page-header")}) {
                    Br()
                    H2(attrs = { classes("title")}) {
                        Text("Configurações")
                    }
                }

                Div(attrs = { classes("def-page-body")}) {
                    Div(attrs = { classes("def-page-body-search") }) {

//                Input(type = InputType.Text, attrs = {
//                    classes("formTextInput")
//                    value("2")
////                    onInput { event -> clientAddress = event.value }
//                })
                    }


                    Div(attrs = { classes("def-page-body-main") }) {
//                Div(attrs = { classes("def-page-body-main-left") }) {
//
//                }

                        Div(attrs = { classes("def-page-body-main-right") }) {


                            var configsList = mutableListOf<ConfigDetailsDc>()

                            for((k, v) in sysConfigs) {
                                when (k) {
                                    "percentual_iva" -> configsList.add(ConfigDetailsDc("Percentagem de IVA", "Será calculada nas vendas", v)
                                    )

                                    "active_package" -> {
                                        configsList.add(ConfigDetailsDc("Pacote do sistema (Apenas leitura)", "Sistema executa as funcionalidades do pacote $v", v, true)
                                        )
                                        activeSysPackage = v
                                    }

                                    "alert_min_pro_quantity" -> {
                                        if (activeSysPackage != SysPackages.L.desc) {
                                            configsList.add(ConfigDetailsDc("Alerta de productos", "Alertar quando o producto atingir a sua quantidade minima", v)
                                            )
                                        }
                                    }


                                }

                            }



                            for ((index, divConf) in configsList.withIndex()) {
                                Div(attrs = { classes("defDivConf") }) {
                                    Div(attrs = { classes("defDivConf-title") }) {
                                        Text(divConf.title)
                                    }

                                    Div(attrs = { classes("defDivConf-desc") }) {
                                        Text(divConf.description)
                                    }

                                    if (divConf.readOnly) {
                                        Input(type = InputType.Text, attrs = {
                                            classes("formTextInput")
                                            value(divConf.value)
                                            readOnly()
                                        })
                                    } else {
                                        Input(type = InputType.Number, attrs = {
                                            classes("formTextInput")
                                            value(divConf.value)
                                            onInput { event ->
                                                val thisValue = event.value.toString()
                                                configsList = configsList.toMutableList().apply {
                                                    this[index] = divConf.copy(value = thisValue)
                                                }
                                                divConf.value = thisValue
                                            }
                                        })
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