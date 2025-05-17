package view.modules.reportModule

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import repository.*
import view.state.AppFunctions
import view.state.AppState.isLoading
import view.state.UiState.finalDate
import view.state.UiState.finalTime
import view.state.UiState.initialDate
import view.state.UiState.initialTime
import view.state.UiState.maxModalState
import view.state.UiState.maySendData
import view.state.UiState.modalState
import view.state.UiState.modalTitle
import view.state.AppState.error
import view.state.AppState.owner


@Composable
fun reportsPage(paramData: UserDataAndSys) {

    val reports = ReportsRepository()
    val owners = OwnersRepository()

    var allReportsData by remember { mutableStateOf(listOf<SaleReportItem>()) }
    var filteredReports by mutableStateOf(mutableListOf<SaleReportItem>(
    ))
    var ownerData by remember { mutableStateOf<List<OwnerItem>?>(null) }
    val coroutineScope = rememberCoroutineScope()
    val router = Router.current


    LaunchedEffect(Unit) {
        try {
            val reportsDeffered = async { reports.fetchSaleReports() }
            allReportsData =
                if (paramData.userRole == Role.V.desc) reportsDeffered.await().filter {it.userId == paramData.userId}
                else reportsDeffered.await()
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        } finally {
            initializeDataTable()
            isLoading = false
            ownerData = owners.getOwners()
            console.log("The owners are: $ownerData")
        }
    }
    if (isLoading) {
        loadingModal()
    } else {
        NormalPage(
            showBackButton = true,
            onBackFunc = { router.navigate("/inventories-module") },
            title = "Relatórios de Vendas", pageActivePath = "sidebar-btn-reports",
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
                        filteredReports = allReportsData.filter { it.datetime?.split(" ")[0] == todayDate }.toMutableList()
                        if (filteredReports.isEmpty()) {
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
                            clickableOption("Para Excel") { window.open("http://0.0.0.0:2000/order/export/orders", "_blank") }
                        }
                        clickableOption("Para CSV") { window.open("http://0.0.0.0:2000/order/export/orders/csv", "_blank") }
                        clickableOption("Para Json") { window.open("http://0.0.0.0:2000/order/export/orders/json", "_blank") }
                    }
                }

                button("btnSolid", "Gerar Inventário") {
                    modalTitle = "Inventário de Vendas"
                    modalState = "open-min-modal"
                }
            }) {

//                val filteredReporsData = allReportsData
            if (error == null) {
                if (allReportsData.isEmpty()) {
                    Div(attrs = { classes("centerDiv") }) {
                        Text("Nenhum registro de vendas efectuadas.")
                    }
                } else {
                    Table(attrs = {
                        classes("display", "myTable")
                    }) {
                        Thead {
                            Tr {
                                Th { Text("Producto") }
                                Th { Text("Quantidade") }
                                Th { Text("Sub Total") }
                                Th { Text("Lucro") }
                                Th { Text("Status") }
                                Th { Text("Proprietário") }
                                Th { Text("Usuário") }
                                Th { Text("Data e hora") }
                            }
                        }
                        Tbody {
                            allReportsData.forEach {
                                Tr {
                                    Td { Text(it.productName) }
                                    Td { Text(it.quantity.toString()) }
                                    Td { Text(moneyFormat(it.subTotal)) }
                                    Td { Text(moneyFormat(it.profit)) }
                                    Td { Text(it.status) }
                                    Td { Text(it.ownerName) }
                                    Td { Text(it.userName) }
                                    Td { Text(it.datetime.toString()) }
                                }
                            }
                        }
                    }
                }
            } else if (error != null) {
                Div { Text(error!!) }
            } else {
                Div { Text("Loading...") }
            }
            //
            if (ownerData != null) {
                FilterRecordsByDateTime(modalState, ownerData!!, onCloseModal = {
                    modalState = "c"
                }) {
                    coroutineScope.launch {
                        var teredReports =
                            reports.fetchDateTimeSales(initialDate, initialTime, finalDate, finalTime, owner)
                        if (teredReports.isEmpty()) {
                            recordsNotFound()
                        } else {
                            filteredReports = teredReports.toMutableList()
                            AppFunctions.resetFilterModalFields()
                            teredReports = emptyList()
                        }
                    }
                }
            }

            rightPartForReports("Registros filtrados", maxModalState, onCloseModal = { maxModalState = "closed"}) {
                if (maySendData) reportPaper(paramData.userName, filteredReports)
            }
        }
    }
}
