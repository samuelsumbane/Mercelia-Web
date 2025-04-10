package view.modules.sellModule

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.document
import kotlinx.browser.window
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.KeyboardEvent
import repository.*


@Composable
fun salesPage() {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

//    val clients = ClientRepository(httpClient)
    val orders = SaleRepository(httpClient)

    val products = ProductRepository(httpClient)
    val categories = CategoryRepository(httpClient)
    val clients = ClientRepository(httpClient)


//    var data by remember { mutableStateOf<List<SalesControlItem>?>(null) }
//    var data by remember { mutableStateOf<List<SalesControlItem>?>(null) }

    var ordersData by mutableStateOf(listOf<OrderItem>())
    var ordersItemsData by mutableStateOf(listOf<OrderItemsItem>())
//    var productData by remember { mutableStateOf(emptyList<ProductItem>()) }
    var productData by remember { mutableStateOf(listOf<ProductItem>()) }
//    var productData by mutableStateOf(emptyList<ProductItem>())
    var categoryData by remember { mutableStateOf(emptyList<CategoryItem>()) }
    var clientData by remember { mutableStateOf(emptyList<ClientItem>()) }

    var productList by mutableStateOf(listOf<SellTableItem>())

    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var modalTitle by remember { mutableStateOf("") }
//    var modalState by remember { mutableStateOf("closed") } //closed = "" --------->>
    var modalState by remember { mutableStateOf("open-min-modal") } //closed = "" --------->>
//    var mediumModalState by remember { mutableStateOf("open-medium-modal") } //closed = "" --------->>
    var mediumModalState by remember { mutableStateOf("closed") } //closed = "" --------->>

    var productName by remember { mutableStateOf("") }

    var productId by remember { mutableStateOf(0) }
    var quantity by remember { mutableIntStateOf(0) }

    // Product quantity after add product in card ---------->>
    var availabelQuantity by remember { mutableIntStateOf(0) } //For each product -------->>
    // Product quantity before it be sold ---------->>
    var productQuantity by remember { mutableIntStateOf(0) }
    val totalProQuantityList = mutableMapOf<String, Int>()

    var productCost by remember { mutableDoubleStateOf(0.0) }

    var totalPaid by remember { mutableDoubleStateOf(0.0) }
    var productPrice by remember { mutableDoubleStateOf(0.0) }
    var totalRequest by remember { mutableDoubleStateOf(0.0) }
    var charge by remember { mutableDoubleStateOf(0.0) }
    var descont by remember { mutableDoubleStateOf(0.0) }

    var receivedValue by remember { mutableDoubleStateOf(0.0) }

    var productError by remember { mutableStateOf("") }
    var quantityError by remember { mutableStateOf("") }
    var totalPaidError by remember { mutableStateOf("") }

    var submitBtnText by remember { mutableStateOf("Submeter") }
    var showDialog by remember { mutableStateOf(true) }
//    var dataFechingState by remember { mutableStateOf(false)}
    var orderId by remember { mutableStateOf("") }
    var clientId by remember { mutableStateOf<Int?>(null) }


   var orderItemsList by remember { mutableStateOf(mutableListOf<OrderItemsItemDraft>()) }
    var paymentMethod by remember { mutableStateOf("") }

//   var filterCategoryId by remember { mutableStateOf(0) }
    var saleMode by remember { mutableStateOf(false) }



    var filterCategoryId by remember { mutableStateOf(0) }
//    var filterCategoryId = 0
    val toFilterData = productData

    var filterProducts = remember(productData, filterCategoryId) {
        if (filterCategoryId != 0) {
            toFilterData.filter { it.categoryId == filterCategoryId }
        } else {
            toFilterData // Aqui, não há necessidade de chamar `.toList()`
        }
    }

//    val filterProducts = if (filterCategoryId != 0) toFilterData.filter {
//        it.categoryId == filterCategoryId
//    } else toFilterData.toList()

    fun calcCharge(value: Double) {
        receivedValue = value
        charge = if (value != 0.0 && value >= totalRequest) {
            value - totalRequest
        }
            else 0.00
    }

    val router = Router.current

    NormalPage(title = "Vendas", pageActivePath = "sidebar-btn-sales", hasNavBar = true,
        navButtons = {
        button("btnSolid", "Nova Venda") {
            modalTitle = "Adicionar Producto"
            modalState = "open-min-modal"
            submitBtnText = "Submeter"
            saleMode = true
        }
    }, onBackFunc = { router.navigate("/management") }
    ) {
            LaunchedEffect(Unit) {
                try {
                    ordersData = orders.fetchOrders()
                    initializeDataTable()

                    productData = products.fetchProducts()
                    categoryData = categories.getCategories()
                    clientData = clients.getClients()

                } catch (e: Exception) {
                    error = "Error: ${e.message}"
                }
            }

//            LaunchedEffect(saleMode) {
//                if (saleMode) {
//                    console.log("sale mode changed")
//                    productData = products.fetchProducts()
//                    categoryData = categories.getCategories()
//                    clientData = clients.getClients()
//                }
//            }

        val listener = remember {
            EventListener { event ->
                if ((event as? KeyboardEvent)?.key == "Escape") {
//                console.log("Você pressionou ESC!")
                    productList = emptyList()
                }

                if ((event as? KeyboardEvent)?.key == "Enter") {
                    console.log("Você pressionou enter!")
                }

            }
        }

        DisposableEffect(Unit) {
            document.addEventListener("keydown", listener)
            onDispose {
                document.removeEventListener("keydown", listener)
            }
        }

            if (error == null) {
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
                                Th { Text("Usuário") }
                                Th { Text("Ações") }
                            }
                        }
                        Tbody {
                            ordersData.forEach {
                                Tr {
                                    Td { Text(it.clientName ?: "Sem cliente") }
                                    Td { Text(it.total.twoDigits()) }
                                    Td { Text(it.orderDateTime.toString()) }
                                    Td { Text(it.status) }
                                    Td { Text(it.userName) }
                                    Td {
                                        button("btn", "Ver itens", ButtonType.Button) {
                                            mediumModalState = "open-medium-modal"
                                            coroutineScope.launch {
                                                ordersItemsData = orders.fetchOrderItems(it.id!!)
                                                orderId = ordersItemsData.first().orderId!!
                                            }
//                                        refreshOrdersItemsTable()
                                        }
                                    }
                                }
                            }
                        }
                    }
                }


            } else if (error != null) {
                Div { Text(error!!) }
            }

            Div(attrs = { classes("scrolled", "max-modal", modalState) }) {

                Div(attrs = { classes("max-modal-header") }) {
                    H3(attrs = { classes("max-modal-title") }) { Text("Vender Productos") }
                }

                Div(attrs = { classes("max-modal-body") }) {
                    Form(attrs = {
                        classes("max-modal-body-sellForm")
                        onSubmit { event ->
                            event.preventDefault()

                            coroutineScope.launch {
                                var totalPaidValue = 0.0
                                for (pro in productList) {
                                    val profit = (pro.productPrice - pro.productCost!!) * pro.quantity
                                    orderItemsList.add(
                                        OrderItemsItemDraft(
                                            pro.id,
                                            pro.quantity,
                                            pro.productCost,
                                            pro.productPrice,
                                            pro.subTotal,
                                            profit
                                        )
                                    )
                                    totalPaidValue += pro.subTotal
                                }

                                val resoan = if (descont == 0.0) "Venda Normal" else "Venda com desconto"

                                val saleStatus = orders.saleProduct(
                                    SaleItem(
                                        order = OrderItemDraft(
                                            clientId = clientId,
                                            total = totalPaidValue,
                                            status = "Completo",
                                            reason = resoan,
                                            userId = 1
                                        ),
                                        o_items = orderItemsList
                                    )
                                )

                                if (saleStatus == 201) alert("success", "Sucesso", "Venda feita com sucesso.")
                                else unknownErrorAlert()

                                productList = emptyList()
                                charge = 0.0
                                totalRequest = 0.0
                                receivedValue = 0.0
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
                            //

                            Div(attrs = {
                            }) {
                                Label { Text("Filtrar por categoria") }
                                Select(attrs = {
                                    style { height(33.px) }
                                    classes("formTextInput")
                                    id("selectCategory")
                                    onChange {
//                                        val toSaleItems = productList
                                        val inputValue = it.value
                                        console.log("$inputValue")
                                        inputValue?.let {
                                            filterCategoryId = inputValue.toInt()
                                        }
//                                        productList = toSaleItems
//                                        println(productList)
                                    }
                                }) {
                                    Option("0") { Text("Todas") }
                                    categoryData.forEach {
                                        if (it.name.isNotBlank()) {
                                            Option("${it.id}") { Text(it.name) }
                                        }
                                    }
                                }

                                Label(attrs = { classes("errorText") }) { Text(productError) }
                            }

                            Br()

                            H4 { Text("Productos") }
                            Hr()
                            Br()
                            Div(attrs = { id("leftPart-center") }) {

//                                Table(attrs = { id("sellProTable")}) {
//                                    Tbody(attrs = { id("products-table") }) {
//                                        productData.map { pro ->
                                        filterProducts.forEach { pro ->
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
//                                    }
//                                }
//                            }
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

                                productList.forEach { pro ->
                                    Div(attrs = { classes("center-div-item") }) {
                                        P { Text(pro.name) }
                                        P { Text(pro.quantity.toString()) }
                                        P { Text(pro.productCost.toString()) }
                                        P { Text(pro.productPrice.toString()) }
                                        P { Text(pro.subTotal.toString()) }
                                        P { Text(pro.availableProQuantity.toString()) }
                                        Div(attrs = { classes("productListBtns")}) {
                                            button("deleteButton", "") {
                                                productList = productList.filter { it.id != pro.id }
                                                totalRequest -= pro.subTotal
                                            }

                                            button("addButton", "") {
                                                productList = productList.map { item ->
                                                    if (item.id == pro.id && item.quantity < pro.availableProQuantity!!) {
                                                        item.copy(
                                                            quantity =  item.quantity + 1,
                                                            subTotal = item.subTotal + item.productPrice, // Atualiza o subtotal
                                                        ).also {
                                                            totalRequest += item.productPrice
                                                        }
                                                    } else item
                                                }
                                            }

                                            button("removeButton", "") {
                                                productList = productList.map { item ->
                                                    if (item.id == pro.id && item.quantity > 1) {
                                                        item.copy(
                                                            quantity = item.quantity - 1,
                                                            subTotal = item.subTotal - item.productPrice // Atualiza o subtotal
                                                        ).also {
                                                            totalRequest -= item.productPrice
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
                                        InputType.Number, { event ->
                                            descont = event.value!!.toDouble()
                                        }, ""
                                    )

                                    formDiv("Valor recebido (do comprador)", receivedValue.toString(),
                                        InputType.Number, { event -> calcCharge(event.value!!.toDouble())},
                                        "")


                                Div(attrs = { id("") }) {
                                        Label { Text("Troco (apenas leitura)") }
                                        Input(type = InputType.Number, attrs = {
                                            id("chargeValue")
                                            classes("formTextMediumInput"); value(charge)
                                            readOnly()
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
                                                    if (option.toInt() == 0) {
                                                        clientId = null
                                                    } else {
                                                        clientId = option.toInt()
                                                    }
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
                                            Option("1") {
                                                Text("Cartão")
                                            }
                                        }
                                    }
                                }

                                Hr()
                                Div(attrs = { id("sale-summary") }) {

                                    Div {
                                        H4 { Text("SubTotal do pedido:") }
                                        H4 { Text("$totalRequest MT") }
                                    }

                                    Div {
                                        H4 { Text("Desconto:") }
                                        H4 { Text("$descont MT") }
                                    }

                                    Div {
                                        H4 { Text("Total do pedido:") }
                                        H4 { Text("${totalRequest - descont} MT") }
                                    }
                                }
                                Hr()

                                Div(attrs = { id("sellButtonsControl") }) {
                                    button("closeButton", "Fechar") {
                                        coroutineScope.launch {
                                            ordersData = orders.fetchOrders()
                                        }
                                        modalState = "closed"
                                        saleMode = false
                                    }

                                    button("closeButton", "Imprimir") {

                                    }

                                    button("submitButton", btnText = "Finalizar", ButtonType.Submit)
                                }
                            }
                        }
                    }
                }

                Div(attrs = { classes("max-modal-footer") })
            }


            Div(attrs = { classes("scrolled", "medium-modal", mediumModalState) }) {

                Div(attrs = { classes("medium-modal-header") }) {
                    H3(attrs = { classes("medium-modal-title") }) { Text("Itens de pedidos") }
                }

                Div(attrs = { classes("medium-modal-body") }) {

                    P { Text("ID do pedido: $orderId") }
                    Br()
                    Table(attrs = {
//                        id("ordersItems")
                        classes("display", "myTable")
                    }) {
                        Thead {
                            Tr {
                                Th { Text("Nome_producto") }
                                Th { Text("Quantidade") }
                                Th { Text("Sub Total") }
                                Th { Text("Lucro") }
                            }
                        }
                        Tbody {
                            ordersItemsData.forEach {
                                Tr {
                                    Td { Text(it.productName.toString()) }
                                    Td { Text(it.quantity.toString()) }
                                    Td { Text(it.subTotal.twoDigits()) }
                                    Td { Text(it.profit.twoDigits()) }
                                }
                            }
                        }
                    }
                }

                Div(attrs = { id("closeMediumModal") }) {
                    button("closeButton", "Fechar") {
                        mediumModalState = "closed"
                    }
                }
            }

        }
//    }
}

