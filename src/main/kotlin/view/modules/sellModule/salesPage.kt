package view.modules.sellModule

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.dom.*
import repository.*
import view.state.AppState.isLoading
import view.state.UiState.modalState
import view.state.UiState.modalTitle
import view.state.UiState.submitBtnText
import view.state.AppState.error
import kotlin.collections.filter


@Composable
fun salesPage(userId: Int, userRole: String, sysPackage: String) {

    val orders = SaleRepository()
    var ordersData by remember { mutableStateOf(listOf<OrderItem>()) }
    var ordersItemsData by mutableStateOf(listOf<OrderItemsItem>())
    val toSaleSysPackage by remember { mutableStateOf(sysPackage) }

    val coroutineScope = rememberCoroutineScope()
    var mediumModalState by remember { mutableStateOf("closed") } //closed = "" --------->>
    var orderId by remember { mutableStateOf("") }
    var saleMode by remember { mutableStateOf(false) }
    val router = Router.current
    var fetchDataAgain by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        try {
            val fetched = async { orders.fetchOrders() }
            ordersData = if (userRole == Role.V.desc) fetched.await().filter { it.userId == userId } else fetched.await()
        } catch (e: Exception) {
            error = "Erro: ${e.message}"
        } finally {
            isLoading = false
            fetchDataAgain = false
            initializeDataTable()
        }
    }
    if (isLoading) {
        loadingModal()
    } else {
        NormalPage(
            title = "Vendas", pageActivePath = "sidebar-btn-sales", sysPackage = sysPackage, hasNavBar = true,
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
            if (ordersData.isEmpty()) {
                Div(attrs = { classes("centerDiv") }) {
                    if (userRole == Role.V.desc) {
                        Text("Nenhuma venda por si efectuada foi encontrada.")
                    } else {
                        Text("Nenhum registro de vendas efectuadas.")
                    }
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
                                    button("smallEyeBtn", "", ButtonType.Button, hoverText = "Ver Itens") {
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

            saleItemsModal(orderId, ordersItemsData, mediumModalState) {
                mediumModalState = "closed"
            }
        }

        saleModal(toSaleSysPackage, saleMode, orders, userId, userRole, modalState) {
            modalState = "closed"
            coroutineScope.launch {
                val allOrders = orders.fetchOrders()
                ordersData = if (userRole == Role.V.desc) allOrders.filter { it.userId == userId } else allOrders
            }
            saleMode = false
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