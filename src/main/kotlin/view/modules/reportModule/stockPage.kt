package view.modules.reportModule

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
fun stockPage() {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val stocks = StockRepository(httpClient)
    val users = UserRepository(httpClient)

    var isLoggedIn by remember { mutableStateOf(false) }

    var stockData by mutableStateOf(listOf<StockItem>())
//    var stockData by mutableStateOf<List<StockItem>?>(null) }

//    var productsData by remember { mutableStateOf<List<ProductItem>?>(null) }

    var filteredStocks by mutableStateOf(mutableListOf<StockItem>(
    ))
//    var filteredReports by mutableStateOf(listOf<saleReportItem>())
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var modalTitle by remember { mutableStateOf("") }
    var modalState by remember { mutableStateOf("closed") } //closed = "" --------->>
//    var modalState by remember { mutableStateOf("open-min-modal") } //closed = "" --------->>
    var maxModalState by remember { mutableStateOf("closed") } //closed = "" --------->>
//    var maxModalState by remember { mutableStateOf("open-max-modal") } //closed = "" --------->>
    var maySendData by remember { mutableStateOf(false) }
    var initialDate by remember { mutableStateOf("") }
    var initialTime by remember { mutableStateOf("") }
    var finalDate by remember { mutableStateOf("") }
    var finalTime by remember { mutableStateOf("") }
    var initialDateError by remember { mutableStateOf("") }
    var finalDateError by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val router = Router.current
    var user by remember { mutableStateOf(emptyLoggedUser) }


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
    }

    LaunchedEffect(isLoggedIn) {
        if (isLoggedIn) {
            try {
                isLoading = true
                val stockdataDeffered = async { stocks.getAllStock() }
                stockData = stockdataDeffered.await()
                initializeDataTable()

            } catch (e: Exception) {
                error = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    if (isLoggedIn) {
//        console.log(isLoggedIn)

            NormalPage(
                showBackButton = true,
                onBackFunc = { router.navigate("/basicReportsPage") },
                title = "Movimentos de Estoque", pageActivePath = "sidebar-btn-reports",
                userRole = user.userRole,
                hasNavBar = true, navButtons = {
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
                                        Td { Text(it.branchName) }
                                        Td { Text(it.datetime) }
                                        Td { Text(it.userName) }
                                    }
                                }
                            }
                        }
                    }
                }

//                } else if (error != null) {
//                    Div { Text(error!!) }
//                } else {
//                    Div { Text("Loading...") }
//                }

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

                        formDiv("Data Inicial", initialDate, inputType = InputType.Date, oninput = { event ->
                            initialDate = event.value
                        }, initialDateError)

                        formDiv(
                            "Hora Inicial", initialTime, InputType.Date,
                            oninput = { event -> initialTime = event.value }, ""
                        )

                        Br()

                        formDiv(
                            "Data Final", finalDate, InputType.Date,
                            oninput = { event -> finalDate = event.value }, finalDateError
                        )

                        formDiv(
                            "Hora Final", finalTime, InputType.Date,
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
                                if (maySendData) stockPaper(filteredStocks)
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

                                        //                                Label { }
                                        //                                Div() {
                                        //                                        button("btn", "Salvar em PDF") {
                                        //
                                        //                                        }
                                        //                                }

                                        //                                Div() {
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

    } else userNotLoggedScreen()
}



