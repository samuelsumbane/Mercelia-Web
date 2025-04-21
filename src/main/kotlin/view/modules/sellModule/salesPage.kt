package view.modules.sellModule

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import repository.*


@Composable
fun salesPage(userId: Int, userRole: String) {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val users = UserRepository(httpClient)
    val orders = SaleRepository(httpClient)

    var ordersData by mutableStateOf(listOf<OrderItem>())
    var ordersItemsData by mutableStateOf(listOf<OrderItemsItem>())

    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var modalTitle by remember { mutableStateOf("") }
    var modalState by remember { mutableStateOf("closed") } //closed = "" --------->>
//    var modalState by remember { mutableStateOf("open-min-modal") } //closed = "" --------->>
//    var mediumModalState by remember { mutableStateOf("open-medium-modal") } //closed = "" --------->>
    var mediumModalState by remember { mutableStateOf("closed") } //closed = "" --------->>
    var orderId by remember { mutableStateOf("") }
//    var userId by remember { mutableStateOf(0) }
    var saleMode by remember { mutableStateOf(false) }
    var submitBtnText by remember { mutableStateOf("Submeter") }
//    val filterProducts = if (filterCategoryId != 0) toFilterData.filter {
//        it.categoryId == filterCategoryId
//    } else toFilterData.toList()
    var isOrdersFetched by remember { mutableStateOf(false) }


    val router = Router.current
    var isLoading by remember { mutableStateOf(false) }
    var isLoggedIn by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val fetched = orders.fetchOrders()
            ordersData = fetched
            initializeDataTable()
        } catch (e: Exception) {
            error = "Erro: ${e.message}"
        }
        isLoading = false
    }

//    SearchableSelect(
//        options = listOf("Moçambique", "Angola", "Brasil", "Portugal"),
//        onOptionSelected = { selected ->
//            println("Selecionado: $selected")
//        }
//    )

    NormalPage(
//        showBackButton = true,
//        onBackFunc = { router.navigate("/basicSellPage") },
        title = "Vendas", pageActivePath = "sidebar-btn-sales", hasNavBar = true,
        userRole = userRole,
        navButtons = {
            button("btnSolid", "Nova Venda") {
                modalTitle = "Adicionar Producto"
                modalState = "open-min-modal"
                submitBtnText = "Submeter"
                saleMode = true
            }
        }
    ) {

        //
        //        if (error == null) {
        if (isLoading) {
            Div(attrs = { classes("centerDiv") }) {
                Text("Carregando...")
            }
        } else {
            if (ordersData.isEmpty()) {
                Div(attrs = { classes("centerDiv") }) {
                    Text("Nenhum registro de vendas efectuadas.")
                }
            } else {
                Table(attrs = {
                    classes("display", "myTable")
                }) {
                    Thead {
                        Tr {
                            Th { Text("Cliente") }
                            Th { Text("Total") }
                            Th { Text("Data") }
                            Th { Text("Status") }
                            Th { Text("Sucursal") }
                            Th { Text("Usuário") }
                            Th { Text("Ações") }
                        }
                    }
                    Tbody {
                        ordersData.forEach {
                            Tr {
                                Td { Text(it.clientName ?: "Sem cliente") }
                                Td { Text(moneyFormat(it.total)) }
                                Td { Text(it.orderDateTime.toString()) }
                                Td { Text(it.status) }
                                Td { Text(it.branchName) }
                                Td { Text(it.userName) }
                                Td {
                                    button("eyeBtn", "", ButtonType.Button) {
                                        mediumModalState = "open-medium-modal"
                                        coroutineScope.launch {
                                            ordersItemsData = orders.fetchOrderItems(it.id!!)
                                            orderId = ordersItemsData.first().orderId!!
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        //        } else if (error != null) {
        //            Div { Text(error!!) }
        //        }
//

        saleModal(httpClient, saleMode, orders, userId, modalState) {
            modalState = "closed"
            coroutineScope.launch {
                ordersData = orders.fetchOrders()
            }
            saleMode = false
        }

        saleItemsModal(orderId, ordersItemsData, mediumModalState) {
            mediumModalState = "closed"
        }
    }
}

@Composable
fun summaryDivItem(key: String, value: String) {
    Div {
        H4 { Text("$key:") }
        H4 { Text(value) }
    }
}