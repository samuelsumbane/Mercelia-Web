package view.modules.sellModule

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import io.ktor.client.*
import kotlinx.browser.document
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.*
import org.jetbrains.compose.web.css.height
import org.jetbrains.compose.web.css.px
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLButtonElement
import org.w3c.dom.HTMLFormElement
import org.w3c.dom.events.EventListener
import org.w3c.dom.events.KeyboardEvent
import repository.*

@Composable
fun saleModal(
    httpClient: HttpClient,
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
    var productList by mutableStateOf(listOf<SellTableItem>())
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

    // Product quantity after add product in card ---------->>
    var availabelQuantity by remember { mutableIntStateOf(0) } //For each product -------->>
    var filterCategoryId by remember { mutableStateOf(0) }
    val toFilterData = productData

    var filterProducts = remember(productData, filterCategoryId) {
        if (filterCategoryId != 0) {
            toFilterData.filter { it.categoryId == filterCategoryId }
        } else {
            toFilterData // Aqui, não há necessidade de chamar `.toList()`
        }
    }
//    var formElement: org.w3c.dom.HTMLFormElement? = null
    var formElement by remember { mutableStateOf<HTMLFormElement?>(null) }
    var submitButton by remember { mutableStateOf<HTMLButtonElement?>(null) }

    fun clearFields() {
        productList = emptyList()
        filterCategoryId = 0
        descont = 0.0
        charge = 0.0
        totalRequest = 0.0
        receivedValue = 0.0
    }

    val router = Router.current

    fun branchIdNotFoundAlert() {
        onOkayAlert("warning", "Localização do sistema não definida.", "Defina a localização do sistema na pagina de sucursais") {
            router.navigate("/branches")
        }
    }

    LaunchedEffect(saleMode) {
        if (saleMode) {
            productData = products.fetchProducts()
            categoryData = categories.getCategories()
            clientData = clients.getClients()
            clearFields()
            //
            val branchDeffered = BranchRepository(httpClient).sysLocationId()
            if (branchDeffered == "404" || branchDeffered == "405") branchIdNotFoundAlert() else  sysLocationId = branchDeffered
        }
    }


        val listener = remember {
            EventListener { event ->
                if ((event as? KeyboardEvent)?.key == "Escape") {
                console.log("Você pressionou ESC!")
//                    filterCategoryId = 40
//                    productList = emptyList()
                    clearFields()
//                    formElement?.submit()
                }


//                if ((event as? KeyboardEvent)?.key == "Enter") {
////                    console.log("Você pressionou enter!")
//                    // Can be used to finish the sale.
//
//                }
            }

        }



        DisposableEffect(Unit) {
            document.addEventListener("keydown", listener)
            onDispose {
                document.removeEventListener("keydown", listener)
            }
        }


    fun calcCharge(value: Double) {
        receivedValue = value
        charge = if (value != 0.0 && value >= totalRequest) {
            value - totalRequest
        }
        else 0.00
    }

    Div(attrs = { classes("scrolled", "max-modal", maxModalState) }) {

        Div(attrs = { classes("max-modal-header") }) {
            H3(attrs = { classes("max-modal-title") }) { Text("Vender Productos") }
        }

        Div(attrs = { classes("max-modal-body") }) {
            Form(attrs = {
                classes("max-modal-body-sellForm")
                ref {
                    formElement = it
                    onDispose { formElement = null } // Corrige o erro
                }
//                method(Method.Post)

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

                    Div(attrs = {
                    }) {
                        Label { Text("Filtrar por categoria") }
                        Select(attrs = {
                            style { height(33.px) }
                            classes("formTextInput")
                            id("selectCategory")
                            onChange {
                                val inputValue = it.value
                                inputValue?.let {
                                    filterCategoryId = inputValue.toInt()
                                }
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
                            summaryDivItem("SubTotal do pedido", "$totalRequest MT")
                            summaryDivItem("Desconto", "$descont MT")
                            summaryDivItem("Total do pedido", "${totalRequest - descont} MT")
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

}