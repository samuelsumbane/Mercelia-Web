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
import org.jetbrains.compose.web.attributes.min
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.percent
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.css.width
import org.jetbrains.compose.web.dom.*
import repository.*
import view.modules.reportModule.reportPaper


@Composable
fun payablesPage(userRole: String, sysPackage: String) {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val users = UserRepository()
    val payables = FinanceRepository(httpClient)

    var allPayableData by mutableStateOf(listOf<PayableItem>())
//    var filteredReporsData by mutableStateOf(listOf<SaleReportItem>())
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

    var supplier by remember { mutableStateOf("") }
    var supplierError by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var payValue by remember { mutableDoubleStateOf(0.0) }
    var payValueLabel by remember { mutableDoubleStateOf(0.0) }
    var payValueError by remember { mutableStateOf("") }
    var expirationDate by remember { mutableStateOf("") }
    var expirationDateError by remember { mutableStateOf("") }
    var paymentDate by remember { mutableStateOf("") }
    var paymentDateError by remember { mutableStateOf("") }
    var paymentForm by remember { mutableStateOf("") }
    var payAccountId by remember { mutableIntStateOf(0) }


    val router = Router.current
    var isLoading by remember { mutableStateOf(false) }


    LaunchedEffect(Unit) {
        try {
            isLoading = true
            val dataDeffered = async { payables.allPayables() }
            allPayableData = dataDeffered.await()
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        } finally {
            initializeDataTable()
            isLoading = false
        }
    }

        NormalPage(
            showBackButton = true,
            onBackFunc = { router.navigate("/basicFinancePage") },
            title = "Contas a pagar", pageActivePath = "sidebar-btn-reports",
            sysPackage = sysPackage,
            userRole = userRole,
            hasNavBar = true, navButtons = {
            button("btnSolid", "+ C. Pagar") {
                modalTitle = "Adicionar Conta a Pagar"
                modalState = "open-min-modal"
            }
        }) {
            if (isLoading) {
                Div(attrs = { classes("centerDiv") }) {
                    Text("Carregando...")
                }
            } else {

                if (error == null) {
                    if (allPayableData.isEmpty()) {
                        Div(attrs = { classes("centerDiv") }) {
                            Text("Nenhum registro de contas pagas.")
                        }
                    } else {
                        Table(attrs = {
                            classes("display", "myTable")
                        }) {
                            Thead {
                                Tr {
                                    Th { Text("Fornecedor") }
                                    Th { Text("Descrição") }
                                    Th { Text("Valor") }
                                    Th { Text("D. Pagamento") }
                                    Th { Text("D. Vencimento") }
                                    Th { Text("F. Pagamento") }
                                    Th { Text("Status") }
                                    Th { Text("Ações") }
                                }
                            }
                            Tbody {
                                allPayableData.map {
                                    Tr {
                                        Td { Text(it.fornecedor) }
                                        Td { Text(it.description) }
                                        Td { Text(moneyFormat(it.value)) }
                                        Td { Text(it.payment_date) }
                                        Td { Text(it.expiration_date) }
                                        Td { Text(it.payment_method) }
                                        Td { Text(it.status.toString()) }
                                        Td {
                                            if (it.status == "Vencido") {
                                                button("btn", "⚠\uFE0F Pagar") {

                                                }
                                            }
                                            when (it.status) {
                                                "Vencido" -> button("btn", "⚠\uFE0F Pagar") {
                                                    payValue = it.value
                                                    supplier = it.fornecedor
                                                    description = it.description
                                                    payModal = "open-min-modal"
                                                    payAccountId = it.id.toInt()
                                                }
                                                "Pendente" -> button("btn", "Pagar") {
                                                    payValue = it.value
                                                    supplier = it.fornecedor
                                                    description = it.description
                                                    payModal = "open-min-modal"
                                                    payAccountId = it.id.toInt()
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


// Pay account ------->>
            minModal(payModal, "Pagar Conta") {
                Form(
                    attrs = {
                        classes("modalform")
                        onSubmit { event ->
                            event.preventDefault()
                            coroutineScope.launch {
                                payValueError = if (payValue == 0.0) "O valor a pagar é obrigatório" else ""

                                if (payValueError.isBlank()) {
                                    val payData = IdAndStatus(payAccountId, 2)
                                    val (status, message) = payables.payABill(payData)
                                    when (status) {
                                        201 -> alertTimer(message)
                                        else -> unknownErrorAlert()
                                    }
                                }
                            }

                        }
                    }
                ) {

                    formDivReadOnly("Fornecedor (Apenas leitura)", supplier)

                    formDivReadOnly("Descrição (Apenas leitura)", description)

//                    Div {
//                        P { Text("Valor da conta: ${moneyFormat(payValueLabel)}")}
//                    }

                    formDiv("Valor a pagar", payValue.toString(),
                        InputType.Number, 12, { event ->
                            if (event.value != null) {
                                payValue = event.value!!.toDouble()
                            }
                        }, payValueError
                    )

                    Div {
                        Label { Text("Metodo de Pagamento") }
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
                                allPayableData = payables.allPayables()
                            }
                        }
                        button("submitButton", "Submeter", ButtonType.Submit)
                    }
                    Br()
                }
            }

// Add payable ------->>
            minModal(modalState, "Ad. Conta a pagar") {
                Form(
                    attrs = {
                        classes("modalform")
                        onSubmit { event ->
                            event.preventDefault()
                            coroutineScope.launch {
                                supplierError = if (supplier.isBlank()) "O fornecedor é obrigatório" else ""
                                payValueError = if (payValue == 0.0) "O a pagar é obrigatório" else ""
                                expirationDateError = if (expirationDate.isBlank()) "A data de expiração é obrigatória" else ""
                                paymentDateError = if (supplier.isBlank()) "A data de pagamento é obrigatória" else ""
                                if (supplierError.isBlank() && payValueError.isBlank() && expirationDateError.isBlank() && paymentDateError.isBlank()) {
                                    val payData = PayableDraft(supplier, description, payValue, expirationDate, paymentForm)
                                    val (status, message) = payables.createPayable(payData)
                                    when (status) {
                                        201 -> alertTimer(message)
                                        else -> unknownErrorAlert()
                                    }
                                }
                            }
                        }
                    }
                ) {

                    formDiv("Fornecedor", supplier, InputType.Text, 28,
                        { event -> supplier = event.value}, supplierError
                    )

                    formDiv("Descrição", inputValue = description, inputType = InputType.Text, oninput = { event -> description = event.value}, spanError = ""
                    )

                    formDiv("Valor a pagar", inputValue = payValue.toString(),
                        inputType = InputType.Number, oninput = { event ->
                            if (event.value != null) {
                                payValue = event.value!!.toDouble()
                            }
                        }, spanError = payValueError
                    )

                    formDiv("Data de Expiração", expirationDate, inputType = InputType.Date, oninput = { event ->
                        expirationDate = event.value
                    }, spanError = expirationDateError)

                    Div {
                        Label { Text("Metodo de Pagamento") }
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
                                allPayableData = payables.allPayables()
                            }
                        }
                        button("submitButton", "Submeter", ButtonType.Submit)
                    }
                    Br()
                }
            }
        }
}
