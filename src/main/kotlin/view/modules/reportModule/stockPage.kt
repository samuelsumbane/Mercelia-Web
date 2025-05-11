package view.modules.reportModule

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import repository.*
import view.state.UiState.modalState
import view.state.AppState.error
import view.state.AppState.isLoading
import view.state.UiState.finalDate
import view.state.UiState.finalDateError
import view.state.UiState.finalTime
import view.state.UiState.initialDate
import view.state.UiState.initialDateError
import view.state.UiState.initialTime
import view.state.UiState.maxModalState
import view.state.UiState.maySendData
import view.state.UiState.modalTitle
import kotlin.collections.filter


@Composable
fun stockPage(paramData: UserDataAndSys) {

    val stocks = StockRepository()
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
                multiFilesExportButton(btnText = "Venda de hoje") {
                    P(attrs = {
                        onClick {
                            val input = document.getElementById("dt-search-0") as? HTMLInputElement
                            input?.value = getUserLocalDateString()
                            input?.dispatchEvent(Event("input"))
                        }
                    }) {
                        Text("Filtrar")
                    }

                    P(attrs = {
                        onClick {
                            val todayDate = getUserLocalDateString()
                            filteredStocks = stockData.filter { it.datetime.split(" ")[0] == todayDate }.toMutableList()
                            modalState = "closed"
                            maxModalState = "open-max-modal"
                            maySendData = true
                        }
                    }) {
                        Text("Imprimir")
                    }
                }
                if (paramData.sysPackage != SysPackages.L.desc) {
                    multiFilesExportButton {
                        if (paramData.sysPackage == SysPackages.PO.desc) {
                            P(attrs = {
                                onClick {
                                    window.open("http://0.0.0.0:2000/stocks/export/stocks/excel", "_blank")
                                }
                            }) {
                                Text("Para Excel")
                            }
                        }

                        P(attrs = {
                            onClick { window.open("http://0.0.0.0:2000/stocks/export/stocks/csv", "_blank") }
                        }) {
                            Text("Para CSV")
                        }
                        P(attrs = {
                            onClick { window.open("http://0.0.0.0:2000/stocks/export/stocks/json", "_blank") }
                        }) {
                            Text("Para Json")
                        }
                    }
                }
                button("btnSolid", "Gerar Inventário") {
                    modalTitle = "Inventário de Estoque"
                    modalState = "open-min-modal"
                }
            }) {
//                if (error == null) {
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
                                Th { Text("Quantidade") }
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
                                    Td { Text(it.quantity.toString()) }
                                    Td { Text(it.beforeQty.toString()) }
                                    Td { Text(it.afterQty.toString()) }
                                    Td { Text(cost.toString()) }
                                    Td { Text(price.toString()) }
                                    Td { Text(it.reason) }
                                    Td { Text(it.ownerName) }
                                    Td { Text(it.branchName) }
                                    Td { Text(it.datetime) }
                                    Td { Text(it.userName) }
                                }
                            }
                        }
                    }
                }
            }

            minModal(modalState, modalTitle) {
                Form(
                    attrs = {
                        classes("modalform")
                        onSubmit { event ->
                            event.preventDefault()
                            initialDateError = if (initialDate == "") "A data inicial é obrigatória" else ""
                            finalDateError = if (finalDate == "") "A data final é obrigatória" else ""

                            if (initialDate != "" && finalDate != "") {
                                if (initialTime == "" && finalTime == "") {
                                    initialTime = "00:00"
                                    finalTime = "23:59"
                                    alert(
                                        "info",
                                        "Campos Preenchidos",
                                        "A hora inicial será 00:00 e a hora final será 23:59."
                                    )
                                } else if (initialTime == "") {
                                    initialTime = "00:00"
                                    alert("info", "Campos Preenchido", "A hora inicial será 00:00.")
                                } else if (finalTime == "") {
                                    finalTime = "23:59"
                                    alert("info", "Campos Preenchido", "A hora final será 23:59.")
                                }

                                //                            console.log("$initialDate, $initialTime, $finalDate, $finalTime")
                                coroutineScope.launch {
                                    var teredReports =
                                        stocks.fetchDateTimeStocks(initialDate, initialTime, finalDate, finalTime)

                                    if (teredReports.isEmpty()) {
                                        alert(
                                            "info",
                                            "Registros não encontrados",
                                            "Nenhum registro encontrado com datas selecionadas"
                                        )
                                    } else {
                                        filteredStocks = teredReports.toMutableList()

                                        modalState = "closed"
                                        maxModalState = "open-max-modal"
                                        maySendData = true
                                        initialDate = ""
                                        initialTime = ""
                                        finalDate = ""
                                        finalTime = ""
                                        teredReports = emptyList()
                                    }
                                }
                            } else if (initialDate.isBlank()) {
                                initialDateError = "Selecione a data inicial"
                            } else {
                                finalDateError = "Selecione a data final"
                            }

                        }
                    }
                ) {

                    formDiv("Data Inicial", initialDate, inputType = InputType.Date, 0, oninput = { event ->
                        initialDate = event.value
                    }, initialDateError)

                    formDiv(
                        "Hora Inicial", initialTime, InputType.Time, 0,
                        oninput = { event -> initialTime = event.value }, ""
                    )
                    Br()
                    formDiv(
                        "Data Final", finalDate, InputType.Date, 0,
                        oninput = { event -> finalDate = event.value }, finalDateError
                    )

                    formDiv(
                        "Hora Final", finalTime, InputType.Time, 0,
                        oninput = { event -> finalTime = event.value }, ""
                    )

                    Div(attrs = { classes("min-submit-buttons") }) {
                        button("closeButton", "Fechar") {
                            modalState = "u"
                        }
                        button("submitButton", "Submeter", ButtonType.Submit)
                    }
                }
            }

            Div(attrs = { classes("scrolled", "max-modal", "customizedMaxModal", maxModalState) }) {

                Div(attrs = { classes("max-modal-header") }) {
                    H3(attrs = {
                        classes("max-modal-title")
                        style { color(Color.white) }
                    }) { Text("Stocks filtrados") }
                }

                Div(attrs = { classes("max-modal-body") }) {
                    Form(attrs = {
                        classes("max-modal-body-sellForm")
                        onSubmit { event ->
                            event.preventDefault()
                        }
                    }) {

                        Div(attrs = { id("r-leftPart") }) {
                            if (maySendData) stockPaper(paramData.userName, filteredStocks)
                        }
                        //Right
                        Div(attrs = {
                            id("r-rightPart")
                            style {
                                width(40.percent)
                                property("margin", "0 0 0 auto")
                            }
                        }) {
                            Div(attrs = { id("rightPart-body") }) {
                                Div(attrs = { classes("reportButtons") }) {
                                    Button(attrs = {
                                        id("cancelButton")
                                        onClick {
                                            maxModalState = "closed"
                                        }
                                    }) {
                                        Label(attrs = { classes("btnLabel") }) {
                                            Text("Fechar")
                                        }
                                    }

                                    Button(attrs = {
                                        id("printFatDoc")
                                        onClick { printPaper() }
                                    }) {
                                        Label(
                                            attrs = { classes("btnLabel") }
                                        ) { Text("Imprimir") }
                                    }
                                }
                            }
                        }
                    }
                }
                Div(attrs = { classes("max-modal-footer") })
            }
        }
    }
}