package view.modules.financeModule

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.*
import repository.*


@Composable
fun receivablesPage(userRole: String, sysPackage: String) {


    val receivables = FinanceRepository()
    val commonRepo = CommonRepository()

    var allReceivablesData by mutableStateOf(listOf<ReceivableItem>())
    var filteredReports by mutableStateOf(mutableListOf<SaleReportItem>(
    ))
    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var modalTitle by remember { mutableStateOf("") }
    var modalState by remember { mutableStateOf("closed") } //closed = "" --------->>
    var payModal by remember { mutableStateOf("closed") } //closed = "" --------->>
//    var modalState by remember { mutableStateOf("open-min-modal") } //closed = "" --------->>
    var maxModalState by remember { mutableStateOf("closed") } //closed = "" --------->>
//    var maxModalState by remember { mutableStateOf("open-max-modal") } //closed = "" --------->>

    var client by remember { mutableStateOf("") }
    var clientError by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var receiveValue by remember { mutableDoubleStateOf(0.0) }
    var payValueLabel by remember { mutableDoubleStateOf(0.0) }
    var receiveValueError by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }
    var expirationDateError by remember { mutableStateOf("") }
    var paymentDate by remember { mutableStateOf("") }
    var receivementDateError by remember { mutableStateOf("") }
    var paymentForm by remember { mutableStateOf("") }
    var receiveAccountId by remember { mutableIntStateOf(0) }


    val router = Router.current
    var isLoading by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val dataDeffered = async { receivables.allReceivables() }
            allReceivablesData = dataDeffered.await()
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        } finally {
            initializeDataTable()
            isLoading = false
        }
    }

        NormalPage(
            showBackButton = true,
            onBackFunc = { router.navigate("/finances-module") },
            title = "Contas a receber", pageActivePath = "sidebar-btn-reports",
            sysPackage = sysPackage,
            userRole = userRole,
            hasNavBar = true, navButtons = {
            button("btnSolid", "+ C. Receber") {
                modalTitle = "Adicionar Conta a Receber"
                modalState = "open-min-modal"
            }
        }) {
            if (isLoading) {
                Div(attrs = { classes("centerDiv") }) {
                    Text("Carregando...")
                }
            } else {

                if (error == null) {
                    if (allReceivablesData.isEmpty()) {
                        Div(attrs = { classes("centerDiv") }) {
                            Text("Nenhum registro de contas recebidas.")
                        }
                    } else {
                        Table(attrs = {
                            classes("display", "myTable")
                        }) {
                            Thead {
                                Tr {
                                    Th { Text("Cliente") }
                                    Th { Text("Descrição") }
                                    Th { Text("Valor") }
                                    Th { Text("D. Recebimento") }
                                    Th { Text("D. Vencimento") }
                                    Th { Text("M. Recebimento") }
                                    Th { Text("Status") }
                                    Th { Text("Ações") }
                                }
                            }
                            Tbody {
                                allReceivablesData.map {
                                    Tr {
                                        Td { Text(it.client) }
                                        Td { Text(it.description) }
                                        Td { Text(moneyFormat(it.value)) }
                                        Td { Text(it.expiration_data) }
                                        Td { Text(it.received_data) }
                                        Td { Text(it.received_method) }
                                        Td { Text(it.status.toString()) }
                                        Td {
                                            when (it.status) {
                                                "Vencido" -> button("btn", "⚠\uFE0F receber") {
                                                    receiveValue = it.value
                                                    client = it.client
                                                    description = it.description
                                                    payModal = "open-min-modal"
                                                    receiveAccountId = it.id.toInt()
                                                }
                                                "Pendente" -> button("btn", "receber") {
                                                    receiveValue = it.value
                                                    client = it.client
                                                    description = it.description
                                                    payModal = "open-min-modal"
                                                    receiveAccountId = it.id.toInt()
                                                }
                                                else -> button("btn", "Ver Detalhes") {

                                                }
                                            }

                                        }
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
            }


// receive account ------->>
            minModal(payModal, "Receber a conta") {
                Form(
                    attrs = {
                        classes("modalform")
                        onSubmit { event ->
                            event.preventDefault()
                            coroutineScope.launch {
                                receiveValueError = if (receiveValue == 0.0) "O valor a receber é obrigatório" else ""

                                if (receiveValueError.isBlank()) {
                                    val receiveData = IdAndStatus(receiveAccountId, 2)
                                    val (status, message) = commonRepo.postRequest("$apiReceivablesPath/receive-account-payment", receiveData)
                                    when (status) {
                                        201 -> alertTimer(message)
                                        else -> unknownErrorAlert()
                                    }
                                }
                            }

                        }
                    }
                ) {

                    formDivReadOnly("Cliente (Apenas leitura)", client)

                    formDivReadOnly("Descrição (Apenas leitura)", description)

//                    Div {
//                        P { Text("Valor da conta: ${moneyFormat(payValueLabel)}")}
//                    }

//                    formDiv("Valor a receb", receiveValue.toString(),
//                        InputType.Number, { event ->
//                            if (event.value != null) {
//                                receiveValue = event.value!!.toDouble()
//                            }
//                        }, receiveValueError
//                    )

                    Div {
                        Label { Text("Metodo de Recebimento") }
                        Select(attrs = {
                            style { height(33.px) }
                            classes("formTextInput")
                            id("selectPaymentMethod")
                            onChange {
                                val inputValue = it.value
                                inputValue?.let { paymentForm = it }
                            }
                        }) {
                            Option("0") {
                                Text("Dinheiro")
                            }
                            if (paymentForm.isBlank()) paymentForm = "Dinheiro"
                        }
                    }

                    Div(attrs = { classes("min-submit-buttons") }) {
                        button("closeButton", "Fechar") {
                            payModal = "closed"
                            coroutineScope.launch {
                                allReceivablesData = receivables.allReceivables()
                            }
                        }
                        button("submitButton", "Submeter", ButtonType.Submit)
                    }
                    Br()
                }
            }

// Add receivable ------->>
            minModal(modalState, "Ad. Conta a receber") {
                Form(
                    attrs = {
                        classes("modalform")
                        onSubmit { event ->
                            event.preventDefault()
                            coroutineScope.launch {
                                clientError = if (client.isBlank()) "O cliente é obrigatório" else ""
                                receiveValueError = if (receiveValue == 0.0) "O a receber é obrigatório" else ""
                                expirationDateError = if (expirationDate.isBlank()) "A data de expiração é obrigatória" else ""
                                receivementDateError = if (client.isBlank()) "A data de Recebimento é obrigatória" else ""
                                if (clientError.isBlank() && receiveValueError.isBlank() && expirationDateError.isBlank() && receivementDateError.isBlank()) {

                                    val receiveData = ReceivableDraft(client, description, receiveValue, expirationDate, paymentForm)
                                    val (status, message) = commonRepo.postRequest("$apiReceivablesPath/create-receivable", receiveData)
                                    when (status) {
                                        201 -> alertTimer(message)
                                        else -> unknownErrorAlert()
                                    }
                                }
                            }
                        }
                    }
                ) {

                    formDiv("Cliente", client, InputType.Text, 48,
                        { event -> client = event.value}, clientError
                    )

                    formDiv("Descrição", description, InputType.Text, 0,
                        { event -> description = event.value}, ""
                    )

                    formDiv("Valor a receber", receiveValue.toString(),
                        InputType.Number, 0, { event ->
                            if (event.value != null) {
                                receiveValue = event.value!!.toDouble()
                            }
                        }, receiveValueError
                    )

                    formDiv("Data de Expiração", expirationDate, inputType = InputType.Date, 0, oninput = { event ->
                        expirationDate = event.value
                    }, expirationDateError)

                    Div {
                        Label { Text("Metodo de Recebimento") }
                        Select(attrs = {
                            style { height(33.px) }
                            classes("formTextInput")
                            id("selectPaymentMethod")
                            onChange {
                                val inputValue = it.value
                                inputValue?.let { paymentForm = it }
                            }
                        }) {
                            Option("0") {
                                Text("Dinheiro")
                            }
                            if (paymentForm.isBlank()) paymentForm = "Dinheiro"
                        }
                    }

                    Div(attrs = { classes("min-submit-buttons") }) {
                        button("closeButton", "Fechar") {
                            modalState = "closed"
                            coroutineScope.launch {
                                allReceivablesData = receivables.allReceivables()
                            }
                        }
                        button("submitButton", "Submeter", ButtonType.Submit)
                    }
                    Br()
                }
            }
        }
}
