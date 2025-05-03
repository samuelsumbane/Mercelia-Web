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
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.attributes.width
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.Event
import repository.*



@Composable
fun reportsPage(paramData: UserDataAndSys) {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val reports = ReportsRepository(httpClient)

    var allReportsData by remember { mutableStateOf(listOf<SaleReportItem>()) }
//    var filteredReporsData by mutableStateOf(listOf<SaleReportItem>())
    var filteredReports by mutableStateOf(mutableListOf<SaleReportItem>(
    ))
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var modalTitle by remember { mutableStateOf("") }
    var modalState by remember { mutableStateOf("closed") } //closed = "" --------->>
//    var modalState by remember { mutableStateOf("open-min-modal") } //closed = "" --------->>
    var maxModalState by remember { mutableStateOf("closed") } //closed = "" --------->>
//    var maxModalState by remember { mutableStateOf("open-max-modal") } //closed = "" --------->>
    var maySendData by remember { mutableStateOf(false) }

    val router = Router.current
    var initialDate by remember { mutableStateOf("") }
    var initialTime by remember { mutableStateOf("") }
    var finalDate by remember { mutableStateOf("") }
    var finalTime by remember { mutableStateOf("") }
    var initialDateError by remember { mutableStateOf("") }
    var finalDateError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val reportsDeffered = async { reports.fetchSaleReports() }
            allReportsData =
                if (paramData.userRole == Role.V.desc) reportsDeffered.await().filter {it.userId == paramData.userId}
                else reportsDeffered.await()
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        } finally {
            initializeDataTable()
            isLoading = false
        }
    }

        NormalPage(
            showBackButton = true,
            onBackFunc = { router.navigate("/basicReportsPage") },
            title = "Relatórios de Vendas", pageActivePath = "sidebar-btn-reports",
            sysPackage = paramData.sysPackage,
            userRole = paramData.userRole,
            hasNavBar = true, navButtons = {

                multiFilesExportButton(btnText = "Venda de hoje") {
                    P(attrs = {
                        onClick {
//                            allReportsData = emptyList()
//                            dt-search-0
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
                            filteredReports = allReportsData.filter { it.datetime?.split(" ")[0] == todayDate }.toMutableList()
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
                                    window.open("http://0.0.0.0:2000/order/export/orders", "_blank")
                                }
                            }) {
                                Text("Para Excel")
                            }
                        }

                        P(attrs = {
                            onClick { window.open("http://0.0.0.0:2000/order/export/orders/csv", "_blank") }
                        }) {
                            Text("Para CSV")
                        }
                        P(attrs = {
                            onClick { window.open("http://0.0.0.0:2000/order/export/orders/json", "_blank") }
                        }) {
                            Text("Para Json")
                        }
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
                            //                id("reportsPageTable")
                            classes("display", "myTable")
                        }) {
                            Thead {
                                Tr {
                                    Th { Text("Producto") }
                                    Th { Text("Quantidade") }
                                    Th { Text("Sub Total") }
                                    Th { Text("Lucro") }
                                    Th { Text("Status") }
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


            minModal(modalState, "Selecionar Intervalo de Datas") {
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

                                //                            maxModalState = "open-max-modal"
                                coroutineScope.launch {
                                    var teredReports =
                                        reports.fetchDateTimeSales(initialDate, initialTime, finalDate, finalTime)
//                                    console.log(teredReports)

                                    if (teredReports.isEmpty()) {
                                        alert(
                                            "info",
                                            "Registros não encontrados",
                                            "Nenhum registro encontrado com datas selecionadas"
                                        )
                                    } else {
                                        filteredReports = teredReports.toMutableList()
                                        modalState = "closed"
                                        maxModalState = "open-max-modal"
                                        maySendData = true
                                        initialDate = ""
                                        initialTime = ""
                                        finalDate = ""
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
                    H3(attrs = { classes("max-modal-title") }) { Text("Registros filtrados") }
                }

                Div(attrs = { classes("max-modal-body") }) {
                    Form(attrs = {
                        classes("max-modal-body-sellForm")
                        onSubmit { event ->
                            event.preventDefault()
                        }
                    }) {

                        Div(attrs = { id("r-leftPart") }) {
                            if (maySendData) reportPaper(paramData.userName, filteredReports)
                        }

                        //Right
                        Div(attrs = {
                            id("r-rightPart")
                            style {
                                //                            backgroundColor(Color.blue)
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



