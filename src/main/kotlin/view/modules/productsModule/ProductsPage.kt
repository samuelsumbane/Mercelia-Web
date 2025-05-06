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
import view.Afiliates.OwnersPage


@Composable
fun productsPage(userRole: String, sysPackage: String) {

    val products = ProductRepository()
    val categories = CategoryRepository()
    val owners = OwnersRepository()
    val commonRepo = CommonRepository()

    var productsData by remember { mutableStateOf<List<ProductItem>?>(null) }
    var categoriesData by remember { mutableStateOf<List<CategoryItem>?>(null) }
    var ownerData by remember { mutableStateOf<List<OwnerItem>?>(null) }
    var sysPackage by remember { mutableStateOf("") }

    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var modalTitle by remember { mutableStateOf("") }
    var modalUpdatePriceTitle by remember { mutableStateOf("") }
    var modalUpdateProTitle by remember { mutableStateOf("") }
    var modalIncreaseStockTitle by remember { mutableStateOf("") }
    var modalMoreDetailsTitle by remember { mutableStateOf("") }

    var addProModalState by remember { mutableStateOf("closed") } //closed = "" --------->>

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
    var ownerId by remember { mutableStateOf(0) }
    var newOwnerId by remember { mutableStateOf(0) }
    var ownerProduct by remember { mutableStateOf("") }

    var proNameError by remember { mutableStateOf("") }
    var proCostError by remember { mutableStateOf("") }
    var proPriceError by remember { mutableStateOf("") }
    var proQuantityError by remember { mutableStateOf("") }
    var minProQuantityError by remember { mutableStateOf("") }
    var submitBtnText by remember { mutableStateOf("Submeter") }
    var categoryError by remember { mutableStateOf("") }
    var ownerError by remember { mutableStateOf("") }

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
        } finally {
              ownerData = owners.getOwners()
        }
    }

    NormalPage(title = "Productos",
        showBackButton = true,
        onBackFunc = { router.navigate("/basicProductsPage") },
        pageActivePath = "sidebar-btn-products",
        sysPackage = sysPackage,
        userRole = userRole,
        hasMain = true, hasNavBar = true, navButtons = {
            if (userRole != Role.V.desc) {
                button("btnSolid", "+ Producto") {
                    modalTitle = "Adicionar Producto"
                    addProModalState = "open-min-modal"
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
                    if (sysPackage == SysPackages.L.desc) {
                        cardWG(title = item.name,
                            warningClass = "empty",
                            cardButtons = {
                                if (userRole != Role.V.desc) {
                                    cardButtons(
                                        showDetailsButton = true,
                                        onSeeDetails = {
                                            modalMoreDetailsState = "open-min-modal"
                                            modalMoreDetailsTitle = "Detalhes do producto"
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
                            if (userRole != Role.V.desc) {
                                CardPitem("Custo", moneyFormat(item.cost))
                            }
                            CardPitem("Preço", moneyFormat(item.price))
                            CardPitem("Estoque", item.stock.toString())
                            CardPitem("Categoria", item.categoryName.toString())
                            CardPitem("C. Barras", item.barcode)
                        }
                    } else {
                        val minProStock = item.minStock ?: -1
                        val warningClass = if (minProStock != -1 && item.stock <= minProStock) "card-warning" else "empty"

                        cardWG(title = item.name,
                            warningClass,
                            cardButtons = {
                                if (userRole != Role.V.desc) {
                                    cardButtons(
                                        showDetailsButton = true,
                                        onSeeDetails = {
                                            modalMoreDetailsState = "open-min-modal"
                                            modalMoreDetailsTitle = "Detalhes do producto"
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
                            if (userRole != Role.V.desc) {
                                CardPitem("Custo", moneyFormat(item.cost))
                            }
                            CardPitem("Preço", moneyFormat(item.price))
                            CardPitem("Estoque", item.stock.toString())
                            CardPitem("Categoria", item.categoryName.toString())
                            CardPitem("C. Barras", item.barcode)
                        }
                    }

                }
            }

        } else if (error != null) {
            Div { Text(error!!) }
        } else {
            Div { Text("Loading...") }
        }

        minModal(addProModalState, modalTitle) {
            Form(
                attrs = {
                    classes("modalform")
                    onSubmit { event ->
                        event.preventDefault()

                        proNameError = if (proName.isBlank()) "O nome do producto é obrigatório" else ""
                        categoryError = if (categoryId == 0) "Selecione a categoria" else ""
                        proCostError = if (costValue == 0.0) "O custo é obrigatório" else ""
                        proPriceError = if (proPrice == 0.0) "O preço do producto é obrigatório" else ""
                        ownerError = if (ownerId == 0) "O proprietário do producto é obrigatório" else ""

                        if (proNameError.isBlank() && proCostError.isBlank() && proPriceError.isBlank() && categoryError.isBlank() && ownerError.isBlank()) {
                            coroutineScope.launch {
                                val minProQuantityChecker = if (minProQuantity == 0) null else minProQuantity
                                val (status, message) = commonRepo.postRequest("$apiProductsPath/create-product",
                                    ProductItem(
                                        id = null, proName, costValue, proPrice, proQuantity, minProQuantityChecker, categoryId, categoryName = null, proBarcode, ownerId, ""
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

                formDiv("Nome do producto", proName, InputType.Text, 48, { event -> proName = event.value}, proNameError)

                formDiv("Quantidade", proQuantity.toString(), InputType.Number, 0, { event ->
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

                if (sysPackage != SysPackages.L.desc) {
                    formDiv("Quantidade minima", minProQuantity.toString(), InputType.Number, 0,{ event ->
                        minProQuantity = event.value!!.toInt()
                    }, minProQuantityError)
                } else minProQuantity = 0

                formDiv("Custo", costValue.toString(), InputType.Number, 0, { event ->
                    costValue = event.value!!.toDouble()
                    if (proQuantity != 0) {
                        totalPaid = proQuantity * costValue
                    }
                }, proCostError)

                formDiv("Preço", proPrice.toString(), InputType.Number, 0, { event ->
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

                formDiv("Código de Barras", proBarcode, InputType.Text, 48, { event -> proBarcode = event.value
                }, "")

                Div(attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        flexDirection(FlexDirection.Column)
                    }
                }) {
                    Label { Text("Proprietário") }
                    Select(attrs = {
                        style { height(33.px) }
                        id("selectOwner")
                        classes("formTextInput")
                        onChange {
                            val inputValue = it.value
                            if (inputValue == "0") {
                                ownerError = "Por favor, selecione um proprietário"
                                return@onChange
                            }

                            inputValue?.let {
                              ownerError = ""
                              ownerId = inputValue.toInt()
                            }
                        }
                    }) {
                        Option("0") {
                            Text("Selecione um proprietário")
                        }

                        ownerData?.map {
                            Option("${it.id}") { Text(it.name) }
                        }
                    }
                    Label(attrs = { classes("errorText") }) { Text(ownerError) }
                }

                Div(attrs = { classes("min-submit-buttons") }) {
                    button("closeButton", "Fechar") {
                        addProModalState = "u"
                        coroutineScope.launch {
                            productsData = products.fetchProducts()
                        }
                    }
                    button("submitButton", submitBtnText, ButtonType.Submit)
                }
                Br()
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
                    P { Text(moneyFormat(costValue)) }
                })

                modalPItem("Preço", value = {
                    P { Text(moneyFormat(proPrice)) }
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
                if (sysPackage != SysPackages.L.desc) {
                    modalPItem("Estoque Min", value = {
                        P { Text(minProQuantity.toString()) }
                    })
                }
                Br()
                Hr()
                modalPItem("Proprietário", value = {
                    P { Text(ownerProduct) }
                })
                modalPItem("", value = {
                    button("btn", "") {
                        modalMoreDetailsState = "closed"
                        modalIncreaseStockState = "open-min-modal"
                        modalIncreaseStockTitle = "Aumentar Estoque"
                        proQuantity = 0
                    }
                })

                //
                modalPItem("Pro/Despromover à", value = {
                    Div(attrs = {
                        style {
                            display(DisplayStyle.Flex)
                            flexDirection(FlexDirection.Column)
                        }
                    }) {
                        Label { Text("Proprietário") }
                        Select(attrs = {
                            style { height(33.px) }
                            id("selectOwner")
                            classes("formTextInput", "inputTitleLabel")
                            onChange {
                                val inputValue = it.value
                                if (inputValue == "0") {
                                    ownerError = "Por favor, selecione um proprietário"
                                    return@onChange
                                }

                                inputValue?.let {
                                    ownerError = ""
                                    newOwnerId = inputValue.toInt()
                                }
                            }
                        }) {
                            Option("0") {
                                Text("Selecione um proprietário")
                            }

                            ownerData?.map {
                                Option("${it.id}") { Text(it.name) }
                            }
                        }
                        Label(attrs = { classes("errorText") }) { Text(ownerError) }
                    }

                    if (ownerId != newOwnerId) {
                        button("checkButton", "") {
                            coroutineScope.launch {
//                                val (status, message) = users.changeUserRole(
//                                    ChangeRoleDC(newRole, userId)
//                                )
//                                alertStatusAndMessageResponse(status, message)
//                                newRole = ""
                            }
                        }
                    }
                })

//                button("checkButton", "") {
//                    coroutineScope.launch {
//                        val (status, message) = users.changeUserRole(
//                            ChangeRoleDC(newRole, userId)
//                        )
//                        alertStatusAndMessageResponse(status, message)
//                        newRole = ""
//                    }
//                }

                submitButtons(submitBtnText) {
                    modalMoreDetailsState = "closed"
                    coroutineScope.launch {
                        productsData = products.fetchProducts()
                    }
                }
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
//                                val minProQuantityChecker = if (minProQuantity == 0) null else minProQuantity

                                if (proId != 0) { //Have to edit ------------>>
                                    val (status, message) = commonRepo.postRequest("$apiProductsPath/increase-stock",
                                        IncreaseProductStockDraft(
                                            proId, costValue, proPrice, proQuantity, "Aumento de Estoque", 1,
                                        ), "put"
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

//                formDiv("Quantidade Adicional", proQuantity.toString(), InputType.Number, { event ->
//                    proQuantity = if (event.value.toString() != "") {
//                        event.value!!.toInt()
//                    } else {
//                        0
//                    }
//                }, proQuantityError)
//
                if (sysPackage != SysPackages.L.desc) {
                    formDiv("Quantidade minima", minProQuantity.toString(), InputType.Number, 0,{ event ->
                        minProQuantity = event.value!!.toInt()
                    }, minProQuantityError)
                } else minProQuantity = 0

                formDiv("Custo", costValue.toString(), InputType.Number, 0, { event ->
                    costValue = event.value!!.toDouble()
                    if (proQuantity != 0) {
                        totalPaid = proQuantity * costValue
                    }
                }, proCostError)

                formDiv("Preço", proPrice.toString(), InputType.Number, 0, { event ->
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
                                    val (status, message) = commonRepo.postRequest("$apiProductsPath/change-product-name-and-category",
                                        ProductNameAndCategory(proId, proName, categoryId, proBarcode), "put"
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

                    formDiv("Nome do producto", proName, InputType.Text, 98, { event -> proName = event.value}, proNameError)
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
                            classes("formTextInput", "inputTitleLabel")
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

                    formDiv("Código de Barras", proBarcode, InputType.Text, 48, { event ->
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
                                        val (status, message) = commonRepo.postRequest("$apiProductsPath/change-product-price",
                                            ChangeProductPriceDraft(proId, proPrice), "put"
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

                    formDiv("Novo Preço", proPrice.toString(), InputType.Number, 0, { event ->
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
