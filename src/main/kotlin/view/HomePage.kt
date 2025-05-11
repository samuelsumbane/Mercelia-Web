package view

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.document
import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.get
import repository.*
import view.state.AppState.isLoading


@Serializable
data class ProductsMostBought(
    val productname: String,
    val quantity: String
)

@Serializable
data class TopAfiliateDC(
    val name: String,
    val quantity: String,
)

@Serializable
data class MonthlyProfitDC(
    val month: String,
    val year: String,
    val profit: String
)

@Serializable
data class MonthlyQuantityDC(
    val month: String,
    val year: String,
    val quantity: Int
)

@Composable
fun homeScreen(userRole: String, userName: String, sysPackage: String) {

    val router = Router.current

    val reports = ReportsRepository()
    val users = UserRepository()
    val products = ProductRepository()

    var productsData by remember { mutableStateOf<List<ProductItem>?>(null) }
    var totalProfit by remember { mutableDoubleStateOf(0.0) }
    var totalSales by remember { mutableDoubleStateOf(0.0)}
    var activeAfiliates by remember { mutableIntStateOf(0) }
    var suspendedAfiliates by remember { mutableIntStateOf(0) }
    var allAfiliatesCount by remember { mutableIntStateOf(0) }

    var totalClients by remember { mutableIntStateOf(0)}
    var totalSuppliers by remember { mutableIntStateOf(0)}

    var data by remember { mutableStateOf<List<JsonObject>?>(null) }
    var soldData by remember { mutableStateOf<List<JsonObject>?>(null) }
    var topAfiliatesData by remember { mutableStateOf<List<JsonObject>?>(null) }
    var topAfiliatesNamesLabels by remember { mutableStateOf<Array<String>>(emptyArray()) }
    var topAfiliatesQtdsLabels by remember { mutableStateOf<Array<String>>(emptyArray()) }

    //getTotalProductSold
    var soldProductsData by remember { mutableStateOf<List<JsonObject>?>(null) }
    var soldProductsLabels by remember { mutableStateOf<Array<String>>(emptyArray()) }
    var soldProductsQtdsLabels by remember { mutableStateOf<Array<String>>(emptyArray()) }

    var salesQuantitiesMonthsLabels by remember { mutableStateOf<Array<String>>(emptyArray()) }

    var salesProfitsMonthsLabels by remember { mutableStateOf<Array<String>>(emptyArray()) }
    var salesProfitsMonthsValues by remember { mutableStateOf<Array<String>>(emptyArray()) }
    var showNotificationAlert by remember { mutableStateOf(true)}

    val coroutineScope = rememberCoroutineScope()
    var showPerfilDiv by remember { mutableStateOf(false) }
    var showThemeModeChooserDiv by remember { mutableStateOf(false) }
    var actualTheme by remember { mutableStateOf(localStorage.getItem("system_theme"))}
    val currentActualThemeName =
        when (actualTheme) {
            "Light" -> "Claro"
            "Dark" -> "Escuro"
            else -> "Auto"
        }
    LaunchedEffect(Unit) {
        if (userRole != Role.V.desc) {
            try {
                val userStates = users.getUserStatus()
                allAfiliatesCount = userStates.second
                activeAfiliates = userStates.first

                suspendedAfiliates = allAfiliatesCount - activeAfiliates
                //
                val (clientsValue, suppliersValue) = reports.getTotalClientsAndSuppliers()
                totalClients = clientsValue
                totalSuppliers = suppliersValue

                val (profitValue, salesValue) = reports.getTotalProfitAndSales()
                totalProfit = profitValue
                totalSales = salesValue

                topAfiliatesData = reports.getUsersTotalSales()
                val topAfiliatesSalesData: Array<TopAfiliateDC> = Json.decodeFromString(topAfiliatesData.toString())
                topAfiliatesNamesLabels = topAfiliatesSalesData.map { it.name }.toTypedArray()
                topAfiliatesQtdsLabels = topAfiliatesSalesData.map { it.quantity }.toTypedArray()
                showTopUsers(topAfiliatesNamesLabels, topAfiliatesQtdsLabels)

                // Monthly profit
                val totalProfitsByMonthAndYear = reports.getEachProductTotalProfit()
                val totalProfitsByMonthAndYearData: Array<MonthlyProfitDC> =
                    Json.decodeFromString(totalProfitsByMonthAndYear.toString())
                salesProfitsMonthsLabels = totalProfitsByMonthAndYearData.map {
                    "${numberToStringMonth(it.month)} - ${it.year}"
                }.toTypedArray()
                salesProfitsMonthsValues = totalProfitsByMonthAndYearData.map { it.profit }.toTypedArray()
                showMonthlyProfits(salesProfitsMonthsLabels, salesProfitsMonthsValues)

                // Monthly sales
                val totalQuantitiesByMonthAndYear = reports.getTotalQuantitiesByMonthAndYear()
                val totalQuantitiesByMonthAndYearData: Array<MonthlyQuantityDC> =
                    Json.decodeFromString(totalQuantitiesByMonthAndYear.toString())
                salesQuantitiesMonthsLabels = totalQuantitiesByMonthAndYearData.map {
                    "${numberToStringMonth(it.month)} - ${it.year}"
                }.toTypedArray()
                val salesQuantitiesMonthsValues: Array<Int> = totalQuantitiesByMonthAndYearData.map { it.quantity }.toTypedArray()
                showMonthlySales(salesQuantitiesMonthsLabels, salesQuantitiesMonthsValues)

                // Sold products --------->>
                soldProductsData = reports.getProductsMostBoughts()
                val productsSoldData: Array<ProductsMostBought> = Json.decodeFromString(soldProductsData.toString())
                soldProductsLabels = productsSoldData.map { it.productname }.toTypedArray()
                soldProductsQtdsLabels = productsSoldData.map { it.quantity }.toTypedArray()
                showSoldProductChart(soldProductsLabels, soldProductsQtdsLabels)

                isLoading = false
                //
                if (sysPackage != SysPackages.L.desc) {
                    productsData = products.fetchProducts()
                        .filter { it.minStock != null && it.stock < it.minStock }
                }

            } catch (e: Exception) {
                console.log("Error: ${e.message}")
            }
        }
    }


    if (isLoading) {
        loadingModal()
    } else {
        Menu(activePath = "sidebar-btn-home", userRole, sysPackage)
        //
        div(divClasses = listOf("content-container", "dash-container")) {
            Header {
                div("header-top") {
                    productsData?.let { pro ->
                        if (pro.isNotEmpty()) {
                            if (showNotificationAlert) {
                                Button(attrs = {
                                    id("closeNotificationAlertButton")
                                    onClick { showNotificationAlert = false }
                                }) {
                                    Text("X")
                                }
                                div("notificationAlertDiv") {
                                    div("notificationAlertDiv-content") {

                                        P(attrs = { id("notificationAlertDiv-content-title") }) { Text("Productos com estoque baixo") }

                                        Hr()

                                        div(divClasses = listOf("p-content")) {
                                            pro.take(4).forEach {
                                                P() { Text(it.name) }
                                            }
                                        }

                                        Br()
                                        if (pro.size > 4) {
                                            button("btn", "Ver mais") {
                                                router.navigate("/products")
                                            }
                                        }
                                    }
                                }
                            }

                        }
                    }

                    div("header-top-perfil-div"){
                        Button(attrs = {
                            id("header-top-perfil-div-btn")
                            onClick {
                                showPerfilDiv = !showPerfilDiv
                            }
                        }) {
                            H3 {
                                val letter = userName[0]
                                Text(letter.toString())
                            }
                        }
                        if (showPerfilDiv) {
                            div("user-perfil-options") {
                                P(attrs = { id("userNameLabel") }) {
                                    Text(userName)
                                }

                                button("bt", "Perfil") {
                                    router.navigate("/eachUser")
                                }

                                button("bt", "Tema: $currentActualThemeName") {
                                    showThemeModeChooserDiv = !showThemeModeChooserDiv
                                }

                                button("bt", "Sair") {
                                    coroutineScope.launch {
                                        val (status, message) = users.logout()
                                        if (status == 200) {
                                            sessionStorage.removeItem("jwt_token")
                                            router.navigate("/")
                                        } else {
                                            alert("error", "Erro", message)
                                        }
                                    }
                                }
                            }
                            if (showThemeModeChooserDiv) {
                                OptionsDiv("themeModeOptions"){
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
                    }
                }

                div("header-bottom") {
                    div("afiliatesInfo") {
                        Div {
                            H4(attrs = { id("allUsersP") }) {
                                Text("Usuários")
                            }
                        }

                        div("afiliatesInfo-divs") {
                            afStatusIndicator("Usuários Activos", "active-Status", activeAfiliates)
                            afStatusIndicator("Usuários Bloqueados", "suspended-Status", suspendedAfiliates)
                            afStatusIndicator("Todos Usuários", "allUsers-Status", allAfiliatesCount)
                        }
                    }

                    homeDivMinResume("expensesInfo", "Parceiros", "Clientes",
                        "$totalClients", "Fornecedores", "$totalSuppliers")

                    homeDivMinResume("profits", "Ganhos Totais", "Vendas",
                        "${moneyFormat(totalSales)} MT", "Lucros", "${moneyFormat(totalProfit)} MT")
                }
            }

            Br()

            Main {
                div("divCharts") {
                    div("fChartsDiv") {
                        div("chart1") {
                            Canvas(attrs = { id("monthlySalesQuantities") })
                        }

                        div("chart2") {
                            Canvas(attrs = { id("topSales") })
                        }
                    }

                    div("sChartsDiv") {
                        div("chart3") {
                            Canvas(attrs = { id("topUsers") })
                        }

                        div("chart4") {
                            Canvas(attrs = { id("monthlyProfits") })
                        }
                    }
                }
            }
        }
    }

}



fun setThemeMode(mode: String) {
    when (mode) {
        "Light" -> document.documentElement?.classList?.remove("dark")
        "Dark" -> document.documentElement?.classList?.add("dark")
        "Auto" -> {
            val prefersDark = window.matchMedia("(prefers-color-scheme: dark)").matches;
            if (prefersDark) {
                document.documentElement?.classList?.add("dark");
            }
        }
    }
    localStorage.setItem("system_theme", mode)
}