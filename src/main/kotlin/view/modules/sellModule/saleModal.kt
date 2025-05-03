package view.modules.sellModule

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import io.ktor.client.*
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.Color
import org.jetbrains.compose.web.css.color
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.HTMLInputElement
import org.w3c.dom.events.KeyboardEvent
import repository.*
import kotlin.collections.listOf

data class ProItem(
    val proId: String,
    val proName: String,
)

@Composable
fun saleModal(
    httpClient: HttpClient,
    sysPackage: String,
    saleMode: Boolean,
    orders: SaleRepository,
    userId: Int,
    maxModalState: String,
    onCloseModal: () -> Unit,
) {

    val products = ProductRepository(httpClient)
    val categories = CategoryRepository(httpClient)
    val clients = ClientRepository(httpClient)


    val coroutineScope = rememberCoroutineScope()
//    var productList by mutableStateOf(listOf<SellTableItem>())
    var productList by remember { mutableStateOf(listOf<SellTableItem>()) }

    var clientId by remember { mutableStateOf<Int?>(null) }
    var orderItemsList by remember { mutableStateOf(mutableListOf<OrderItemsItemDraft>()) }
    var paymentMethod by remember { mutableStateOf("") }

    // Product quantity before it be sold ---------->>
    val totalProQuantityList = mutableMapOf<String, Int>()
    var totalRequest by remember { mutableDoubleStateOf(0.0) }
    var charge by remember { mutableDoubleStateOf(0.0) }
    var descont by remember { mutableDoubleStateOf(0.0) }
    var receivedValue by remember { mutableDoubleStateOf(0.0) }
    var productError by remember { mutableStateOf("") }
    var productData by remember { mutableStateOf(emptyList<ProductItem>()) }
//    var productData by remember { mutableStateOf(listOf<ProductItem>()) }
//    var productData by remember { mutableStateOf<emptyList<ProductItem>>(null) }

    var categoryData by remember { mutableStateOf(emptyList<CategoryItem>()) }
    var clientData by remember { mutableStateOf(emptyList<ClientItem>()) }
    var sysLocationId by remember { mutableStateOf("") }

    var submitBtnText by remember { mutableStateOf("Submeter") }
    var productId by remember { mutableStateOf(0) }
    var proSelectedItem = emptyList<ProItem>()
    // Product quantity after add product in card ---------->>
    var availabelQuantity by remember { mutableIntStateOf(0) } //For each product -------->>
    var filterCategoryId by remember { mutableStateOf(0) }
    var query by remember { mutableStateOf("") }

    var formElement by remember { mutableStateOf<HTMLFormElement?>(null) }
    var submitButton by remember { mutableStateOf<HTMLButtonElement?>(null) }
    val inputRef = remember { mutableStateOf<HTMLInputElement?>(null) }

    val branches = BranchRepository(httpClient)
    var branchDeffered by remember { mutableStateOf("") }

    fun clearFields() {
        productList = emptyList()
        descont = 0.0
        charge = 0.0
        totalRequest = 0.0
        receivedValue = 0.0
        query = ""
    }

    val router = Router.current

    fun branchIdNotFoundAlert() {
        onOkayAlert("warning", "Localização do sistema não definida.", "Defina a localização do sistema na pagina de sucursais") {
            router.navigate("/branches")
        }
    }

    fun calcCharge(value: Double) {
        receivedValue = value
        charge = if (value != 0.0 && value >= totalRequest) {
            value - totalRequest
        }
        else 0.00
    }

    LaunchedEffect(Unit) {
        document.addEventListener("keydown", { event ->
            if (event is KeyboardEvent && event.key == "/") {
                event.preventDefault()
                document.getElementById("searchInput")?.let {
                    (it as? HTMLInputElement)?.focus()
                }
            }

            if (event is KeyboardEvent && event.key == "Escape") {
                clearFields()
            }

            if (event is KeyboardEvent && event.key in listOf("1", "2", "3", "4", "5", "6", "7", "8", "9", "0")) {
                val activeElement = document.activeElement

                if (activeElement?.tagName == "INPUT" && activeElement.id != "receivedValueInput") return@addEventListener

                event.preventDefault()
                document.getElementById("receivedValueInput")?.let {
                    (it as? HTMLInputElement)?.focus()
                }
                receivedValue = (receivedValue.toString() + event.key).toDouble()
                coroutineScope.launch {
                    calcCharge(receivedValue)
                }
            }
        })
    }


    LaunchedEffect(saleMode) {
        if (saleMode) {
            productData = products.fetchProducts()
            categoryData = categories.getCategories()
            clientData = clients.getClients()
            clearFields()
            branchDeffered = branches.sysLocationId()
//            if (branchDeffered == "404" || branchDeffered == "405") branchIdNotFoundAlert() else  sysLocationId = branchDeffered
            if (branchDeffered != "404" && branchDeffered != "405") {
                sysLocationId = branchDeffered
            }
        }
    }


    if (branchDeffered != "404" && branchDeffered != "405") {
        val filterProducts = remember(productData, query) {
            if (query.isNotBlank()) {
                productData.filter { it.name.startsWith(query, ignoreCase = true) }
            } else {
                productData
            }
        }

        Div(attrs = { classes("scrolled", "max-modal", maxModalState) }) {

            Div(attrs = { classes("max-modal-header") }) {
                H3(attrs = { classes("max-modal-title") }) { Text("Vender Productos") }
            }

            Div(attrs = { classes("max-modal-body") }) {
                Form(attrs = {
                    classes("max-modal-body-sellForm")

                    onSubmit { event ->
                        event.preventDefault()
                        coroutineScope.launch {

                            if (productList.isEmpty()) {
                                alert("info", "Venda não realizada", "Nenhum producto foi encontrado.")
                            } else {
                                var totalPaidValue = 0.0
                                for (pro in productList) {
                                    val profit = (pro.productPrice - pro.productCost!!) * pro.quantity
                                    orderItemsList.add(
                                        OrderItemsItemDraft(
                                            pro.id, pro.quantity, pro.productCost,
                                            pro.productPrice, pro.subTotal, profit
                                        )
                                    )
                                    totalPaidValue += pro.subTotal
                                }

                                val resoan = if (descont == 0.0) "Venda Normal" else "Venda com desconto"

                                if (sysLocationId.isBlank()) {
                                    branchIdNotFoundAlert()
                                } else {
                                    val saleStatus = orders.saleProduct(
                                        SaleItem(
                                            order = OrderItemDraft(
                                                clientId = clientId,
                                                total = totalPaidValue,
                                                status = "Completo",
                                                reason = resoan,
                                                userId = userId,
                                                branchId = sysLocationId.toInt()
                                            ),
                                            o_items = orderItemsList
                                        )
                                    )

                                    if (saleStatus == 201) alertTimer("Venda feita com sucesso.")
                                    else unknownErrorAlert()

                                    clearFields()
                                    productData = products.fetchProducts()
                                }
                            }
                        }
                    }
                }) {

                    Div(attrs = { id("leftPart") }) {
                        Input(type = InputType.Hidden, attrs = {
                            value(productId)
                            onInput { event -> productId = event.value.toInt() }
                        })
                        //
                        totalProQuantityList.forEach {
                            Label(attrs = { classes("") }) { Text("${it.key} - ${it.value}") }
                        }
                        //
                        Div(attrs = { id("leftPart-title") }) {
                            H3{ Text("Lista de productos") }
                        }
                        Hr()
                        Br()

                        Div(attrs = {
                        }) {
                            Input(type = InputType.Text, attrs = {
                                id("searchInput")
                                classes("formTextInput")
                                placeholder("Pesquisar...")
                                value(query)
                                onInput { event -> query = event.value }
                            })
                        }
                        Br()
                        Div(attrs = { id("leftPart-center") }) {
                            filterProducts.forEach { pro ->
                                if (pro.stock > 0) {
                                    Div(attrs = { classes("productToSaleItem") }) {
                                        P { Text(pro.name) }
                                        Div {
                                            button("throwRight", "") {
                                                val productExists = productList.firstOrNull() { it.id == pro.id!! }
                                                if (productExists != null) {
                                                    alert("info", "Producto adicionado", "O producto já foi adicionado")
                                                } else {
                                                    val product = SellTableItem(
                                                        id = pro.id!!,
                                                        name = pro.name,
                                                        quantity = 1,
                                                        productCost = pro.cost,
                                                        productPrice = pro.price,
                                                        formatToTwoDecimalPlaces(pro.price),
                                                        availableProQuantity = pro.stock,
                                                    )
                                                    productList = productList + product
                                                    totalRequest += pro.price
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }

                    // Center (horizontally)
                    Div(attrs = { id("center") }) {
                        Div(attrs = {
                            classes("scolled", "sellTable")
                        }) {
                            Div(attrs = { classes("center-div-item", "center-div-item-title") }) {
                                H4 { Text("Nome") }
                                H4 { Text("Quantidade") }
                                H4 { Text("Custo") }
                                H4 { Text("Preço") }
                                H4 { Text("Sub-Total") }
                                H4 { Text("Qtd. Disponível") }
                                H4 { Text("Ações") }
                            }

                            productList.forEach { sellItem ->
                                Div(attrs = { classes("center-div-item") }) {
                                    P { Text(sellItem.name) }
                                    P { Text(sellItem.quantity.toString()) }
                                    P { Text(moneyFormat(sellItem.productCost!!)) }
                                    P { Text(moneyFormat(sellItem.productPrice)) }
                                    P { Text(moneyFormat(sellItem.subTotal)) }
                                    P { Text(sellItem.availableProQuantity.toString()) }
                                    Div(attrs = { classes("productListBtns")}) {
                                        button("deleteButton", "") {
                                            productList = productList.filter { it.id != sellItem.id }
                                            totalRequest -= sellItem.subTotal
                                        }

                                        button("addButton", "") {
                                            productList = productList.map { item ->
                                                if (item.id == sellItem.id && item.quantity < sellItem.availableProQuantity!!) {
                                                    item.copy(
                                                        quantity =  item.quantity + 1,
                                                        subTotal = item.subTotal + sellItem.productPrice,
                                                    ).also {
                                                        totalRequest += sellItem.productPrice
                                                    }
                                                } else item
                                            }
                                        }

                                        button("removeButton", "") {
                                            productList = productList.map { item ->
                                                if (item.id == sellItem.id && item.quantity > 1) {
                                                    item.copy(
                                                        quantity = item.quantity - 1,
                                                        subTotal = item.subTotal - sellItem.productPrice // Atualiza o subtotal
                                                    ).also {
                                                        totalRequest -= sellItem.productPrice
                                                    }
                                                } else item
                                            }
                                        }
                                    }
                                }
                            }
                        }
//                            Div(attrs = { id("sellControl")}) {
                        Div(attrs = { classes("chargeAndDiscont")}) {
                            P()
                            formDiv("Desconto", descont.toString(),
                                InputType.Number, 0, { event ->
                                    val inputValue = (event.value as? String)?.toDoubleOrNull()
                                    if (inputValue != null && inputValue >= 0) {
                                        descont = inputValue
                                    }
                                }, ""
                            )

                            Div {
                                Label { Text("Valor recebido (do comprador)") }
                                Input(type = InputType.Number, attrs = {
                                    id("receivedValueInput")
                                    classes("formTextInput")
                                    value(receivedValue)
                                    min("0")
                                    onInput { event ->
                                        val inputValue = (event.value as? String)?.toDoubleOrNull()
                                        if (inputValue != null && inputValue >= 0) {
                                            calcCharge(inputValue)
                                        }
                                    }
                                })
                            }
                            P()
                        }
                    }

                    //Right
                    Div(
                        attrs = { id("rightPart")
                            classes("scrolled")
                        }
                    ) {
                        Div(attrs = { id("rightPart-title") }) {
                            H3{
                                Text("Resumo e Pagamento")
                            }
                        }
                        Hr()
                        Br()
                        Div(attrs = { id("rightPart-body") }) {
                            Div(attrs = { id("rightPartInputs") }) {
                                Div {
                                    Label { Text("Cliente") }
                                    Br()
                                    Select(attrs = {
                                        style { height(33.px) }
                                        classes("formTextMediumInput")
                                        id("selectPaymentMethod")
                                        onChange {
                                            val inputValue = it.value
                                            inputValue?.let { option ->
                                                clientId = if (option.toInt() == 0) null else option.toInt()
                                            }
                                        }
                                    }) {
                                        Option("0") {
                                            Text("Sem Cliente")
                                        }
                                        clientData.forEach { client ->
                                            Option("${client.id}") {
                                                Text(client.name)
                                            }
                                        }
                                    }
                                }
                                Br()
                                Div {
                                    Label { Text("Metôdo de pagamento") }
                                    Br()
                                    if (sysPackage == SysPackages.L.desc) {
                                        formDivReadOnly("Dinheiro", "")
                                    } else {
                                        Select(attrs = {
                                            style { height(33.px) }
                                            classes("formTextMediumInput")
                                            id("selectPaymentMethod")
                                            onChange {
                                                val inputValue = it.value
                                                inputValue?.let { paymentMethod = it }
                                            }
                                        }) {
                                            Option("0") {
                                                Text("Dinheiro")
                                            }
                                        }
                                    }
                                }
                            }

                            Hr()
                            Div(attrs = { id("sale-summary") }) {
                                summaryDivItem("SubTotal do pedido", "${moneyFormat(totalRequest)} MT")
                                summaryDivItem("Desconto", "${moneyFormat(descont)} MT")
                                summaryDivItem("Troco", "${moneyFormat(charge)} MT")
                                Br()

                                Hr()
                                Br()
                                Div {
                                    H4 { Text("Total do pedido (MT):") }
                                    H2(attrs = {style { color(Color.green) }}) { Text("${moneyFormat(totalRequest - descont)}") }
                                }
                            }
                            Hr()

                            Div(attrs = { id("sellButtonsControl") }) {
                                button("closeButton", "Fechar") { onCloseModal() }

                                button("submitButton", btnText = "Finalizar", ButtonType.Submit)
//                            Button(
//                                attrs = {
//                                    ref {
//                                        submitButton = it
//                                        onDispose { submitButton = null }
//                                    }
//                                    onClick {
//                                        console.log("Botão Finalizar clicado")
//                                        formElement?.submit()
//                                    }
//                                }
//                            ) {
//                                Text("Finalizar")
//                            }
                            }
                        }
                    }
                }
            }
            Div(attrs = { classes("max-modal-footer") })
        }
    } else branchIdNotFoundAlert()


}