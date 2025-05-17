package view.modules.reportModule

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.width
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import repository.*
import view.state.AppFunctions
import view.state.UiState.modalState
import view.state.AppState.error
import view.state.AppState.isLoading
import view.state.AppState.owner
import view.state.UiState.finalDate
import view.state.UiState.finalTime
import view.state.UiState.initialDate
import view.state.UiState.initialTime
import view.state.UiState.maxModalState
import view.state.UiState.maySendData
import view.state.UiState.modalTitle
import kotlin.collections.filter


@Composable
fun stockPage(paramData: UserDataAndSys) {

    val stocks = StockRepository()
    val owners = OwnersRepository()
    var ownerData by remember { mutableStateOf<List<OwnerItem>?>(null) }

    var stockData by mutableStateOf(listOf<StockItem>())
    var filteredStocks by mutableStateOf(mutableListOf<StockItem>(
    ))
    val coroutineScope = rememberCoroutineScope()
    val router = Router.current

    LaunchedEffect(Unit) {
        try {
            val stockdataDeffered = async { stocks.getAllStock() }
            stockData = if (paramData.userRole == Role.V.desc) stockdataDeffered.await().filter { it.userId == paramData.userId } else stockdataDeffered.await()
            initializeDataTable()
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        } finally {
            isLoading = false
            ownerData = owners.getOwners()
        }
    }

    if (isLoading) {
        loadingModal()
    } else {
        NormalPage(
            showBackButton = true,
            onBackFunc = { router.navigate("/inventories-module") },
            title = "Movimentos de Estoque", pageActivePath = "sidebar-btn-reports",
            sysPackage = paramData.sysPackage,
            userRole = paramData.userRole,
            hasNavBar = true, navButtons = {
                optionsColectionDiv(btnText = "Venda de hoje") {
                    clickableOption("Filtrar") {
                        val input = document.getElementById("dt-search-0") as? HTMLInputElement
                        input?.value = getUserLocalDateString()
                        input?.dispatchEvent(Event("input"))
                    }

                    clickableOption("Imprimir") {
                        val todayDate = getUserLocalDateString()
                        filteredStocks = stockData.filter { it.datetime.split(" ")[0] == todayDate }.toMutableList()
                        if (filteredStocks.isEmpty()) {
                            todayRecordsNotFound()
                        } else {
                            modalState = "closed"
                            maxModalState = "open-max-modal"
                            maySendData = true
                        }
                    }
                }
                if (paramData.sysPackage != SysPackages.L.desc) {
                    optionsColectionDiv {
                        if (paramData.sysPackage == SysPackages.PO.desc) {
                            clickableOption("Para Excel") { window.open("http://0.0.0.0:2000/stocks/export/stocks/excel", "_blank") }
                        }
                        clickableOption("Para CSV") { window.open("http://0.0.0.0:2000/stocks/export/stocks/csv", "_blank") }
                        clickableOption("Para Json") { window.open("http://0.0.0.0:2000/stocks/export/stocks/json", "_blank") }
                    }
                }
                button("btnSolid", "Gerar Inventário") {
                    modalTitle = "Inventário de Estoque"
                    modalState = "open-min-modal"
                }
            }) {

            if (isLoading) {
                Text("Carregando...")
            } else {
                if (stockData.isEmpty()) {
                    Div(attrs = { classes("centerDiv") }) {
                        Text("Nenhum registro de movimento de estoque.")
                    }
                } else {
                    Table(attrs = {
                        classes("display", "myTable")
                    }) {
                        Thead {
                            Tr {
                                Th { Text("Producto") }
                                Th { Text("Tipo") }
                                Th{ Text("Quant.") }
                                Th { Text("Qtd Antes") }
                                Th { Text("Qtd Depois") }
                                Th { Text("Custo") }
                                Th { Text("Preço") }
                                Th { Text("Razão") }
                                Th { Text("Proprietário") }
                                Th { Text("Sucursal") }
                                Th { Text("Data e hora") }
                                Th { Text("Usuário") }
                            }
                        }
                        Tbody {
                            stockData.forEach {
                                val cost = moneyFormat(it.cost) ?: 0.0
                                val price = moneyFormat(it.price) ?: 0.0
                                Tr {
                                    Td { Text(it.productName) }
                                    Td { Text(it.type) }
                                    Td{ Text(it.quantity.toString()) }
                                    Td { Text(it.beforeQty.toString()) }
                                    Td { Text(it.afterQty.toString()) }
                                    Td { Text(cost.toString()) }
                                    Td { Text(price.toString()) }
                                    Td { Text(it.reason) }
                                    Td { Text(it.ownerName) }
                                    Td { Text(it.branchName.cut(5)) }
                                    Td { Text(it.datetime) }
                                    Td { Text(it.userName) }
                                }
                            }
                        }
                    }
                }
            }

            if (ownerData != null) {
                FilterRecordsByDateTime(modalState, ownerData!!, onCloseModal = {
                    modalState = "c"
                }) {
                    coroutineScope.launch {
                        var teredReports =
                            stocks.fetchDateTimeStocks(initialDate, initialTime, finalDate, finalTime, owner)
                        if (teredReports.isEmpty()) {
                            recordsNotFound()
                        } else {
                            filteredStocks = teredReports.toMutableList()
                            AppFunctions.resetFilterModalFields()
                            teredReports = emptyList()
                        }
                    }
                }
            }

            rightPartForReports("Estoques filtrados", maxModalState, onCloseModal = { maxModalState = "closed"}) {
                if (maySendData) stockPaper(paramData.userName, filteredStocks)
            }
        }
    }
}