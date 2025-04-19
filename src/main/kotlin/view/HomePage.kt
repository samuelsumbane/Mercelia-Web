package view

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
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
    val quantity: String
)


@Composable
fun homeScreen() {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val reports = ReportsRepository(httpClient)
    val users = UserRepository(httpClient)
//    var checkSession by remember { mutableStateOf(false) }


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

    //
    var salesQuantitiesMonthsLabels by remember { mutableStateOf<Array<String>>(emptyArray()) }
    var salesQuantitiesMonthsValues by remember { mutableStateOf<Array<String>>(emptyArray()) }

    var salesProfitsMonthsLabels by remember { mutableStateOf<Array<String>>(emptyArray()) }
    var salesProfitsMonthsValues by remember { mutableStateOf<Array<String>>(emptyArray()) }
    var isLoggedIn by remember { mutableStateOf(false) }
    var user by remember { mutableStateOf(emptyLoggedUser) }
    val router = Router.current
    val coroutineScope = rememberCoroutineScope()


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
            if (user.userRole != Role.V.desc) {
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
                    salesQuantitiesMonthsValues = totalQuantitiesByMonthAndYearData.map { it.quantity }.toTypedArray()
                    showMonthlySales(salesQuantitiesMonthsLabels, salesQuantitiesMonthsValues)

                    // Sold products --------->>
                    soldProductsData = reports.getProductsMostBoughts()
                    console.log(soldProductsData)
                    val productsSoldData: Array<ProductsMostBought> = Json.decodeFromString(soldProductsData.toString())
                    soldProductsLabels = productsSoldData.map { it.productname }.toTypedArray()
                    soldProductsQtdsLabels = productsSoldData.map { it.quantity }.toTypedArray()
                    showSoldProductChart(soldProductsLabels, soldProductsQtdsLabels)

                } catch (e: Exception) {
                    console.log("Error: ${e.message}")
                }
            }
        }
    }


//    if (isU)
    var message by remember { mutableStateOf("Pressione ESC para limpar a mensagem.") }

    // Criamos um objeto EventListener para armazenar a referência exata
//    val listener = remember {
//        EventListener { event ->
//            if ((event as? KeyboardEvent)?.key == "Escape") {
//                console.log("Você pressionou ESC!")
//            }
//            if ((event as? KeyboardEvent)?.key == "Enter") {
//                console.log("Você pressionou enter!")
//            }
//
//        }
//    }
//
//    DisposableEffect(Unit) {
//        document.addEventListener("keydown", listener)
//        onDispose {
//            document.removeEventListener("keydown", listener)
//        }
//    }

    if (isLoggedIn) {
        if (user.userRole == Role.V.desc) {
            userHasNotAccessScreen()
        } else {
            console.log(user.userRole)
            Menu(activePath = "sidebar-btn-home", user.userRole)
            //
            Div(attrs = { classes("content-container", "dash-container") }) {
                Header {
//                Div(attrs = { id("header-top") }) {
//                    H3() {
//                        val letter = user.userName[0]
//                        Text(letter.toString())
//                    }
//                }
                    Button(attrs = {
                        id("header-top")

                    }) {
                        H3 {
                            val letter = user.userName[0]
                            Text(letter.toString())
                        }
                        Div(attrs = { id("user-perfil-options") }) {
                            button("bt", "Perfil") {
                                router.navigate("/eachUser")
                            }

                            button("bt", "Sair") {
                                users.logout().also {
                                    router.navigate("/")
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
                            "${totalSales.twoDigits<Double>()} MT", "Lucros", "${totalProfit.twoDigits<Double>()} MT")
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

    } else {
        userNotLoggedScreen()
    }

}