package view

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.sessionStorage
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import org.jetbrains.compose.web.dom.*
import repository.*


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
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val reports = ReportsRepository(httpClient)
    val users = UserRepository(httpClient)
    val products = ProductRepository(httpClient)

    var productsData by remember { mutableStateOf<List<ProductItem>?>(null) }

    var sysConfigs by remember { mutableStateOf(emptyList<SysConfigItem>()) }
    var activeSysPackage by remember { mutableStateOf(sysPackage) }

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

    val router = Router.current
    val coroutineScope = rememberCoroutineScope()
    var isLoading by remember { mutableStateOf(true)}

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

                if (activeSysPackage != SysPackages.L.desc) {
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
        Menu(activePath = "sidebar-btn-home", userRole, activeSysPackage)
        //
        Div(attrs = { classes("content-container", "dash-container") }) {
            Header {
                Div(attrs = { id("header-top") }) {

                    productsData?.let { pro ->
                        if (pro.isNotEmpty()) {
                            if (showNotificationAlert) {
                                Button(attrs = {
                                    id("closeNotificationAlertButton")
                                    onClick { showNotificationAlert = false }
                                }) {
                                    Text("X")
                                }
                                Div(attrs = { id("notificationAlertDiv") }) {

                                    Div(attrs = { id("notificationAlertDiv-content") }) {

                                        P(attrs = { id("notificationAlertDiv-content-title") }) { Text("Productos com estoque baixo") }

                                        Hr()

                                        Div(attrs = { classes("p-content") }) {
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

                    Button(attrs = {
                        id("header-top-perfil-div")
                    }) {
                        H3 {
                            val letter = userName[0]
                            Text(letter.toString())
                        }
                        Div(attrs = { id("user-perfil-options") }) {
                            P(attrs = { id("userNameLabel") }) {
                                Text(userName)
                            }

                            button("bt", "Perfil") {
                                router.navigate("/eachUser")
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
                    }

                }


                Div(attrs = { id("header-bottom") }) {
                    Div(attrs = { id("afiliatesInfo") }) {
                        Div {
                            H4(attrs = { id("allUsersP") }) {
                                Text("Usuários")
                            }
                        }

                        Div(attrs = { id("afiliatesInfo-divs") }) {
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
                Div(attrs = { id("divCharts") }) {
                    Div(attrs = { id("fChartsDiv") }) {
                        Div(attrs = { id("chart1") }) {
                            Canvas(attrs = { id("monthlySalesQuantities") })
                        }

                        Div(attrs = { id("chart2") }) {
                            Canvas(attrs = { id("topSales") })
                        }
                    }

                    Div(attrs = { id("sChartsDiv") }) {
                        Div(attrs = { id("chart3") }) {
                            Canvas(attrs = { id("topUsers") })
                        }

                        Div(attrs = { id("chart4") }) {
                            Canvas(attrs = { id("monthlyProfits") })
                        }
                    }
                }
            }
        }
    }

}