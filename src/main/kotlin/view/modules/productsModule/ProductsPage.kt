package view.modules.productsModule

import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.coroutines.launch
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.HTMLSelectElement
import kotlinx.browser.document
import repository.*


@Composable
fun productsPage(userRole: String) {

    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }

    val products = ProductRepository(httpClient)
    val categories = CategoryRepository(httpClient)

    var productsData by remember { mutableStateOf<List<ProductItem>?>(null) }
    var categoriesData by remember { mutableStateOf<List<CategoryItem>?>(null) }

    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var modalTitle by remember { mutableStateOf("") }
    var modalUpdatePriceTitle by remember { mutableStateOf("") }
    var modalUpdateProTitle by remember { mutableStateOf("") }
    var modalIncreaseStockTitle by remember { mutableStateOf("") }
    var modalMoreDetailsTitle by remember { mutableStateOf("") }
    var modalState by remember { mutableStateOf("closed") } //closed = "" --------->>
    var modalUpdatePriceState by remember { mutableStateOf("closed") }
    var modalUpdateProState by remember { mutableStateOf("closed") }
    var modalIncreaseStockState by remember { mutableStateOf("closed") }
    var modalMoreDetailsState by remember { mutableStateOf("closed") }

    var proId by remember { mutableStateOf(0) }
    var proName by remember { mutableStateOf("") }
    var proPurchacePrice by remember { mutableDoubleStateOf(0.0) }
    var proQuantity by remember { mutableIntStateOf(0) }
    var minProQuantity by remember { mutableIntStateOf(0) }
    var categoryId by remember { mutableIntStateOf(0) }
    var categoryName by remember { mutableStateOf("") }

    var totalPaid by remember { mutableDoubleStateOf(0.0) }
    var costValue by remember { mutableDoubleStateOf(0.0) }
    var proPrice by remember { mutableDoubleStateOf(0.0) }
    var proBarcode by remember { mutableStateOf("") }

    var proNameError by remember { mutableStateOf("") }
    var proCostError by remember { mutableStateOf("") }
    var proPriceError by remember { mutableStateOf("") }
    var proQuantityError by remember { mutableStateOf("") }
    var minProQuantityError by remember { mutableStateOf("") }
    var submitBtnText by remember { mutableStateOf("Submeter") }
    var categoryError by remember { mutableStateOf("") }

    fun cleanVarFields() {
        proId = 0
        proName = ""
        proPurchacePrice = 0.0
        proPrice = 0.0
        proQuantity = 0
        minProQuantity = 0
        categoryId = 0
        totalPaid = 0.0
        costValue = 0.0
        proBarcode = ""
        proNameError = ""
        proCostError = ""
        proPriceError = ""
        proQuantityError = ""
        minProQuantityError = ""
        categoryError = ""
    }

    val router = Router.current

    LaunchedEffect(Unit) {
        try {
            productsData = products.fetchProducts()
            categoriesData = categories.getCategories()
        } catch (e: Exception) {
            error = "Error: ${e.message}"
        }
    }

    NormalPage(title = "Productos",
        showBackButton = true,
        onBackFunc = { router.navigate("/basicProductsPage") },
        pageActivePath = "sidebar-btn-products",
        userRole = userRole,
        hasMain = true, hasNavBar = true, navButtons = {
            if (userRole != Role.V.desc) {
                button("btnSolid", "+ Producto") {
                    modalTitle = "Adicionar Producto"
                    modalState = "open-min-modal"
                    submitBtnText = "Submeter"
                    cleanVarFields()
                }
            }

        }) {

        if (productsData != null) {
            if (productsData.isNullOrEmpty()) {
                Div(attrs = { classes("centerDiv") }) {
                    Text("Nenhum producto encontrado.")
                }
            } else {
                productsData!!.forEach { item ->
                    cardWG(title = "",
                        cardButtons = {
                            if (userRole != Role.V.desc) {
                                cardButtons(
                                    //                            onEditButton = {
                                    //                                proId = item.id!!
                                    //                                proName = item.name
                                    //                                modalState = "open-min-modal"
                                    //                                submitBtnText = "Ver"
                                    //                            },
                                    showDetailsButton = true,
                                    onSeeDetails = {
                                        modalMoreDetailsState = "open-min-modal"
                                        proId = item.id!!
                                        proName = item.name
                                        proQuantity = item.stock
                                        costValue = item.cost
                                        proPrice = item.price
                                        proQuantity = item.stock
                                        minProQuantity = item.minStock ?: 0
                                        categoryName = item.categoryName.toString()
                                        proBarcode = item.barcode
                                    },
                                    showDeleteBtn = false
                                )
                            }

                        }) {
                        CardPitem("Nome", item.name)
                        CardPitem("Preço", item.price.twoDigits())
                        CardPitem("Estoque", item.stock.toString())
                        CardPitem("Categoria", item.categoryName.toString())
                        CardPitem("C. Barras", item.barcode)
                    }
                }
            }

        } else if (error != null) {
            Div { Text(error!!) }
        } else {
            Div { Text("Loading...") }
        }

        minModal(modalState, modalTitle) {
            Form(
                attrs = {
                    classes("modalform")
                    onSubmit { event ->
                        event.preventDefault()

                        proNameError = if (proName.isBlank()) "O nome do producto é obrigatório" else ""
                        categoryError = if (categoryId == 0) "Selecione a categoria" else ""

                        proCostError = if (costValue == 0.0) "O custo é obrigatório" else ""
                        proPriceError = if (proPrice == 0.0) "O preço do producto é obrigatório" else ""

                        if (proNameError == "" && proCostError == "" && proPriceError == "" && categoryError == "") {
                            coroutineScope.launch {
                                val status = products.createProduct(
                                    ProductItem(
                                        id = null, proName, costValue, proPrice, proQuantity, minProQuantity, categoryId, categoryName = null, proBarcode
                                    )
                                )
                                if (status == 201) {
                                    alertTimer("Producto adicionado com sucesso.")
                                } else unknownErrorAlert()
                                cleanVarFields()
                            }

                            val selectElement = document.getElementById("selectCategory") as? HTMLSelectElement
                            selectElement?.selectedIndex = 0
                        }

                    }

                }
            ) {
                Input(type = InputType.Hidden, attrs = {
                    value(proId)
                    onInput { event -> proId = event.value.toInt() }
                })

                formDiv("Nome do producto", proName, InputType.Text, { event -> proName = event.value}, proNameError)

                formDiv("Quantidade", proQuantity.toString(), InputType.Number, { event ->
                    if (event.value.toString() != "") {
                        proQuantity = event.value!!.toInt()
                        if (costValue != 0.0) {
                            totalPaid = proQuantity * costValue
                        }
                    } else {
                        proQuantity = 0
                        totalPaid = 0.00
                    }
                }, proQuantityError)

                formDiv("Quantidade minima", minProQuantity.toString(), InputType.Number, { event ->
                    minProQuantity = event.value!!.toInt()
                }, minProQuantityError)

                formDiv("Custo", costValue.toString(), InputType.Number, { event ->
                    costValue = event.value!!.toDouble()
                    if (proQuantity != 0) {
                        totalPaid = proQuantity * costValue
                    }
                }, proCostError)

                formDiv("Preço", proPrice.toString(), InputType.Number, { event ->
                    proPrice = event.value!!.toDouble()
                }, proPriceError)

                Div(attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        flexDirection(FlexDirection.Column)
                    }
                }) {
                    Label { Text("Categoria") }
                    Select(attrs = {
                        style { height(33.px) }
                        id("selectCategory")
                        classes("formTextInput")
                        onChange {
                            val inputValue = it.value
                            if (inputValue == "0") {
                                categoryError = "Por favor, selecione uma categoria"
                                return@onChange
                            }

                            inputValue?.let {
                                categoryError = ""
                                categoryId = inputValue.toInt()
                            }
                        }
                    }) {
                        Option("0") {
                            Text("Selecione uma categoria")
                        }

                        categoriesData?.map {
                            Option("${it.id}") {
                                Text(it.name)
                            }
                        }
                    }
                    Label(attrs = { classes("errorText") }) { Text(categoryError) }
                }

                formDiv("Código de Barras", proBarcode, InputType.Text, { event ->
                    proBarcode = event.value
                }, "")


                Div(attrs = { classes("min-submit-buttons") }) {
                    button("closeButton", "Fechar") {
                        modalState = "u"
                        coroutineScope.launch {
                            productsData = products.fetchProducts()
                        }
                    }
                    button("submitButton", submitBtnText, ButtonType.Submit)
                }
                Br()
            }
        }

        // Increase product stock --------->>
        minModal(modalIncreaseStockState, modalIncreaseStockTitle) {
            Form(
                attrs = {
                    classes("modalform")
                    onSubmit { event ->
                        event.preventDefault()

                        proCostError = if (costValue == 0.0) "O custo é obrigatório" else ""
                        proPriceError = if (proPrice == 0.0) "O preço do producto é obrigatório" else ""

                        if (proCostError == "" && proPriceError == "") {
                            coroutineScope.launch {
                                if (proId != 0) { //Have to edit ------------>>
                                    val status = products.increaseProductStock(
                                        IncreaseProductStockDraft(
                                            proId, costValue, proPrice, proQuantity, "Aumento de Estoque", 1,
                                        )
                                    )
                                    if (status == 201) {
                                        alertTimer("Novo estoque adicionado com sucesso com valores actualizados.")
                                    } else unknownErrorAlert()
                                }
                                cleanVarFields()
                            }
                            modalIncreaseStockState = "closed"
                        }
                    }
                }
            ) {
                Input(type = InputType.Hidden, attrs = {
                    value(proId)
                    onInput { event -> proId = event.value.toInt() }
                })

                formDiv("Quantidade Adicional", proQuantity.toString(), InputType.Number, { event ->
                    proQuantity = if (event.value.toString() != "") {
                        event.value!!.toInt()
                    } else {
                        0
                    }
                }, proQuantityError)

                formDiv("Quantidade minima", minProQuantity.toString(), InputType.Number, { event ->
                    minProQuantity = event.value!!.toInt()
                }, minProQuantityError)

                formDiv("Custo", costValue.toString(), InputType.Number, { event ->
                    costValue = event.value!!.toDouble()
                    if (proQuantity != 0) {
                        totalPaid = proQuantity * costValue
                    }
                }, proCostError)

                formDiv("Preço", proPrice.toString(), InputType.Number, { event ->
                    proPrice = event.value!!.toDouble()
                }, proPriceError)

                submitButtons(submitBtnText) {
                    modalIncreaseStockState = "closed"
                    modalIncreaseStockTitle = ""
                    coroutineScope.launch {
                        productsData = products.fetchProducts()
                    }
                }
            }
        }

        // More product details --------->>
        minModal(modalMoreDetailsState, modalMoreDetailsTitle) {
            Form(
                attrs = {
                    classes("modalform")
                    onSubmit { event ->
                        event.preventDefault()
                    }
                }
            ) {

                modalPItem("Nome", value = {
                    P { Text(proName) }
                })
                modalPItem("Categoria", value = {
                    P { Text(categoryName) }
                })
                modalPItem("Códgo de Barras", value = {
                    P { Text(proBarcode) }
                })

                modalPItem("", value = {
                    button("btn", "Actualizar Producto") {
                        modalMoreDetailsState = "closed"
                        modalUpdateProState = "open-min-modal"
                        modalUpdateProTitle = "Actualizar Producto"
                    }
                })
                Br()
                Hr()

                modalPItem("Custo", value = {
                    P { Text(costValue.twoDigits()) }
                })

                modalPItem("Preço", value = {
                    P { Text(proPrice.twoDigits()) }
                })

                modalPItem("", value = {
                    button("btn", "Actualizar Preço") {
                        modalMoreDetailsState = "closed"
                        modalUpdatePriceState = "open-min-modal"
                        modalUpdatePriceTitle = "Actualizar Preço"
                    }
                })
                Br()

                Hr()

                modalPItem("Estoque", value = {
                    P { Text(proQuantity.toString()) }
                })

                if (userRole != "Vendedor/Caixa") {
                    modalPItem("", value = {
                        button("btn", "Aumentar Estoque") {
                            modalMoreDetailsState = "closed"
                            modalIncreaseStockState = "open-min-modal"
                            modalIncreaseStockTitle = "Aumentar Estoque"
                            proQuantity = 0
                        }
                    })
                }

                Br()

                Hr()

                modalPItem("Estoque Min", value = {
                    P { Text(minProQuantity.toString()) }
                })


                submitButtons(submitBtnText) {
                    modalMoreDetailsState = "closed"
                    coroutineScope.launch {
                        productsData = products.fetchProducts()
                    }
                }
            }
        }

        // Change ProductName and category --------->>
        minModal(modalUpdateProState, modalUpdateProTitle) {
            Form(
                attrs = {
                    classes("modalform")
                    onSubmit { event ->
                        event.preventDefault()

                        proNameError = if (proName.isBlank()) "O nome do producto é obrigatório" else ""
                        categoryError = if (categoryId == 0) "Selecione a categoria" else ""

                        if (proNameError == "" && categoryError == "") {
                            coroutineScope.launch {
                                if (proId != 0) {
                                    val status = products.changeProductNameAndCategory(
                                        ProductNameAndCategory(proId, proName, categoryId, proBarcode)
                                    )

                                    if (status == 201) {
                                        alertTimer("Producto actualizado com sucesso")
                                        modalUpdateProState = "closed"
                                        modalUpdateProTitle = ""
                                        productsData = products.fetchProducts()
                                    } else unknownErrorAlert()
                                }
                                    cleanVarFields()
                                }
                            } else if (categoryError == "") {
                                alert("error", "Erro", "Por favor, selecione uma categoria.")
                            }


                        }
                    }
                ) {
                    Input(type = InputType.Hidden, attrs = {
                        value(proId)
                        onInput { event -> proId = event.value.toInt() }
                    })

                    formDiv("Nome do producto", proName, InputType.Text, { event -> proName = event.value}, proNameError)
                    Div(attrs = {
                        style {
                            display(DisplayStyle.Flex)
                            flexDirection(FlexDirection.Column)
                        }
                    }) {
                        Label { Text("Categoria") }
                        Select(attrs = {
                            style { height(33.px) }
                            id("selectCategory")
                            classes("formTextInput")
                            onChange {
                                val inputValue = it.value
                                if (inputValue == "0") {
                                    categoryError = "Por favor, selecione uma categoria"
                                    return@onChange
                                }

                                inputValue?.let {
                                    categoryError = ""
                                    categoryId = inputValue.toInt()
                                }
                            }
                        }) {
                            Option("0") {
                                Text("Selecione uma categoria")
                            }

                            categoriesData?.map {
                                Option("${it.id}") {
                                    Text(it.name)
                                }
                            }
                        }

                        Label(attrs = { classes("errorText") }) { Text(categoryError) }
                    }

                    formDiv("Código de Barras", proBarcode, InputType.Text, { event ->
                        proBarcode = event.value
                    }, "")

                    submitButtons(submitBtnText) {
                        modalUpdateProState = "closed"
                        modalUpdateProTitle = ""
                        coroutineScope.launch {
                            productsData = products.fetchProducts()
                        }
                    }
                }
            }

            // Change Product Price --------->>
            minModal(modalUpdatePriceState, modalUpdatePriceTitle) {
                Form(
                    attrs = {
                        classes("modalform")
                        onSubmit { event ->
                            event.preventDefault()
                            proPriceError = if (proPrice == 0.0) "O preço do producto é obrigatório" else ""
                            if (proPriceError == "") {
                                coroutineScope.launch {
                                    if (proId != 0) {
                                        val status = products.updateProductPrice(
                                            ChangeProductPriceDraft(proId, proPrice)
                                        )

                                        if (status == 201) {
                                            alertTimer("Preço do producto actualizado com sucesso")
                                            modalUpdatePriceState = "closed"
                                            modalUpdatePriceTitle = ""
                                            cleanVarFields()
                                        } else unknownErrorAlert()
                                    }
                                }
                            }

                        }
                    }
                ) {
                    Input(type = InputType.Hidden, attrs = {
                        value(proId)
                        onInput { event -> proId = event.value.toInt() }
                    })

                    //            CardPitem("Preço Actual", proPrice.toString())

                    formDiv("Novo Preço", proPrice.toString(), InputType.Number, { event ->
                        proPrice = event.value!!.toDouble()
                    }, proPriceError)

                    submitButtons(submitBtnText) {
                        modalUpdatePriceState = "closed"
                        modalUpdatePriceTitle = ""
                        coroutineScope.launch {
                            productsData = products.fetchProducts()
                        }
                    }
                }
            }
        }
}
