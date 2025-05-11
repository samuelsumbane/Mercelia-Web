package view.modules.settingsModule

import androidx.compose.runtime.Composable
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.ButtonType
//import org.jetbrains.compose.web.attributes.InputType
import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import io.ktor.client.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.browser.localStorage
import kotlinx.coroutines.async
import kotlinx.serialization.json.Json
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.css.*
import org.jetbrains.compose.web.dom.*
import org.w3c.dom.set
import repository.*
import view.state.AppState.isLoading
import view.state.AppState.error
import view.state.UiState.modalState
import view.state.UiState.modalTitle
import view.state.UiState.submitBtnText


@Composable
fun brancesPage(userRole: String, sysPackage: String) {

    val router = Router.current

    val branches = BranchRepository()
    val commonRepo = CommonRepository()

    var branchData by remember { mutableStateOf(emptyList<BranchItem>()) }
    val coroutineScope = rememberCoroutineScope()
    var branchId by remember { mutableStateOf(0) }
    var branchName by remember { mutableStateOf("") }
    var systemLocation by remember { mutableStateOf("") }
    var branchAddress by remember { mutableStateOf("") }

    var branchNameError by remember { mutableStateOf("") }
    var branchAddressError by remember { mutableStateOf("") }

    var appropriateName by remember { mutableStateOf("") }

    fun cleanVarFields() {
        branchId = 0
        branchName = ""
        branchAddress = ""
    }

    LaunchedEffect(Unit) {
        if (userRole != Role.V.desc) {
            try {
                val branchesDeffered = async { branches.allBranches() }
                branchData = branchesDeffered.await()
            } catch (e: Exception) {
                error = "Error: ${e.message}"
            } finally {
                isLoading = false
            }

            appropriateName = if (sysPackage == SysPackages.L.desc) "Sede" else "Sucursal"
        }
    }

    fun locationDataFun() {
        val locationId = localStorage.getItem("system_location")
        if (locationId != null) {
            val branchItemData = branchData.firstOrNull { it.id == locationId.toInt() }
            systemLocation = if (branchItemData != null) {
                "${branchItemData.name} - ${branchItemData.address}"
            } else "Não definido"
        }
    }
    fun closeAndFetch() {
        modalState = "u"
        coroutineScope.launch {
            branchData = branches.allBranches()
        }
    }
    if (isLoading) {
        loadingModal()
    } else {
        normalBranchPage(
            showBackButton = true,
            onBackFunc = { router.navigate("/basicSettingsPage") },
            userRole = userRole,
            hasMain = true, hasNavBar = true, titleDivScope = {
            }, navButtons = {
                val canAddBranch = (sysPackage == "Lite" && branchData.isEmpty()) ||
                        (sysPackage == "Plus" && branchData.size < 5) ||
                        (sysPackage == "Pro")

                if (canAddBranch) {
                    button("btnSolid", "+ $appropriateName") {
                        modalTitle = "Adicionar $appropriateName"
                        modalState = "open-min-modal"
                        submitBtnText = "Submeter"
                        cleanVarFields()
                    }
                }
            }, sysPackage = sysPackage,
            topContent = {
                Div(attrs = {
                    style {
                        display(DisplayStyle.Flex)
                        flexDirection(FlexDirection.Column)
                    }
                }) {
                    if (branchData.isNotEmpty()) {
                        locationDataFun()
                        Label { Text("Localização actual: $systemLocation") }
                        Select(attrs = {
                            style { height(33.px) }
                            id("selectLocation")
                            classes("formTextInput")
                            onChange {
                                val inputValue = it.value
                                if (inputValue == "0") {
                                    //                                categoryError = "Por favor, selecione uma categoria"
                                    return@onChange
                                }

                                inputValue?.let { option ->
                                    //                                categoryError = ""
                                    localStorage["system_location"] = option
                                    locationDataFun()
                                }
                            }
                        }) {

                            Option("0") {
                                Text("")
                            }
                            branchData.forEach {
                                Option("${it.id}") {
                                    Text("${it.name} - ${it.address}")
                                }
                            }
                        }
                        Label(attrs = { classes("errorText") }) { Text("") }
                    }

                }
            }
        ) {
            if (error == null) {
                if (branchData.isEmpty()) {
                    Div(attrs = { classes("centerDiv") }) {
                        Text("Nenhuma sucursal/sede encontrada. ")
                    }
                } else {

                    branchData.forEach { item ->
                        cardWG(
                            title = "",
                            cardButtons = {
                                cardButtons(
                                    onEditButton = {
                                        branchId = item.id
                                        branchName = item.name
                                        branchAddress = item.address
                                        modalState = "open-min-modal"
                                        submitBtnText = "Editar"
                                    },
                                    showDeleteBtn = false
                                )
                            }) {
                            CardPitem("Nome", item.name)
                            CardPitem("Endereço", item.address)
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

                            branchNameError = if (branchName.isBlank()) "O nome é obrigatório" else ""
                            branchAddressError = if (branchAddress.isBlank()) "O endere é obrigatório" else ""

                            if (branchNameError == "" && branchAddressError == "") {
                                coroutineScope.launch {
                                    if (branchId != 0) {
                                        val (updateStatus, message) = commonRepo.postRequest(
                                            "$apiBranchesPath/update-branch",
                                            BranchItem(branchId, branchName, branchAddress),
                                            "put"
                                        )
                                        if (updateStatus == 201) {
                                            alertTimer("$appropriateName actualizada com sucesso.")
                                        } else unknownErrorAlert()
                                        //                                    modalState = "closed"
                                        closeAndFetch()
                                    } else {
                                        val (status, message) = commonRepo.postRequest(
                                            "$apiBranchesPath/create-branch",
                                            BranchItem(
                                                branchId,
                                                branchName,
                                                branchAddress
                                            )
                                        )

                                        when (status) {
                                            101, 102, 103 -> alert("error", "$appropriateName não adicionada", message)
                                            201 -> alertTimer(message)
                                            else -> unknownErrorAlert()
                                        }

                                        if (status == 201) {
                                            alertTimer("$appropriateName adicionada com sucesso.")
                                        } else unknownErrorAlert()
                                    }
                                    cleanVarFields()
                                }
                            }
                        }
                    }
                ) {
                    Input(type = InputType.Hidden, attrs = {
                        value(branchId)
                        onInput { event -> branchId = event.value.toInt() }
                    })

                    formDiv(
                        "Nome da $appropriateName",
                        branchName,
                        InputType.Text, 98,
                        { event -> branchName = event.value },
                        branchNameError
                    )

                    formDiv("Endereço da $appropriateName", branchAddress, InputType.Text, 0, { event ->
                        branchAddress = event.value
                    }, branchAddressError)

                    Div(attrs = { classes("min-submit-buttons") }) {
                        button("closeButton", "Fechar") {
                            closeAndFetch()
                        }
                        button("submitButton", submitBtnText, ButtonType.Submit)
                    }
                    Br()
                }
            }
        }
    }
}
