package view.Afiliates


import androidx.compose.runtime.*
import app.softwork.routingcompose.Router
import components.*
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.dom.*
import repository.*
import view.state.UiState.modalState
import view.state.AppState.error
import view.state.AppState.isLoading
import view.state.UiState.modalTitle
import view.state.UiState.submitBtnText


@Composable
fun OwnersPage(userRole: String, sysPackage: String) {

    val owners = OwnersRepository()
    val commonRepository = CommonRepository()
    var ownerData by remember { mutableStateOf(emptyList<OwnerItem>()) }
    val coroutineScope = rememberCoroutineScope()
    var ownerId by remember { mutableStateOf(0) }
    var ownerName by remember { mutableStateOf("") }
    var ownerNameError by remember { mutableStateOf("") }
    var ownerPhone by remember { mutableStateOf("") }
    val router = Router.current

    LaunchedEffect(Unit) {
        if (userRole != Role.V.desc) {
            try {
                ownerData = owners.getOwners()
            } catch (e: Exception) {
                error = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }

    fun closeAndFetch() {
        coroutineScope.launch { ownerData = owners.getOwners() }
        modalState = "closed"
    }
    if (isLoading) {
        loadingModal()
    } else {
        NormalPage(
            showBackButton = true,
            onBackFunc = { router.navigate("/partners-module") },
            title = "Proprietários", pageActivePath = "sidebar-btn-partners",
            sysPackage = sysPackage,
            userRole = userRole, hasMain = true, hasNavBar = true, navButtons = {
                val canAddOwner = (sysPackage == "Lite" && ownerData.isEmpty()) ||
                        (sysPackage == "Plus" && ownerData.size < 5) ||
                        (sysPackage == "Pro")

                if (canAddOwner) {
                    button("btnSolid", "+ Proprietário") {
                        modalTitle = "Adicionar Proprietário"
                        modalState = "open-min-modal"
                        submitBtnText = "Submeter"
                        ownerNameError = ""
                    }
                }
            }) {

            if (ownerData != null) {
                if (ownerData!!.isEmpty()) {
                    Div(attrs = { classes("centerDiv") }) {
                        Text("Nenhum proprietário encontrado.")
                    }
                }
                ownerData!!.forEach { item ->
                    cardWG(title = "", cardButtons = {
                        cardButtons(
                            onEditButton = {
                                modalTitle = "Editar proprietário"
                                ownerId = item.id!!
                                ownerName = item.name
                                ownerPhone = item.telephone ?: ""
                                modalState = "open-min-modal"
                                submitBtnText = "Editar"
                            },
                            onDeleteButton = {
                                coroutineScope.launch {
                                    val (status, message) = commonRepository.deleteRequest("$apiOwnersPath/delete-owner/${item.id!!}")
                                    when (status) {
                                        200 -> {
                                            alertTimer(message)
                                            ownerData = owners.getOwners()
                                        }

                                        404 -> alert("error", "Proprietário não encontrado.", message)
                                        406 -> alert("warning", "Delete não aceite.", message)
                                    }
                                }
                            }
                        )
                    }) {
                        CardPitem("Nome", item.name)
                        item.telephone?.let {
                            CardPitem("Telefone", item.telephone)
                        }
                    }
                }
            } else if (error != null) {
                Div { Text(error!!) }
            } else {
                Div { Text("Carregando...") }
            }

            minModal(modalState, modalTitle) {
                Form(
                    attrs = {
                        classes("modalform")
                        onSubmit { event ->
                            event.preventDefault()
                            ownerNameError = if (ownerName.isBlank()) "O nome é obrigatório" else ""

                            if (ownerNameError.isBlank()) {
                                coroutineScope.launch {
                                    if (ownerId != 0) {
                                        val (status, message) = commonRepository.postRequest<OwnerItem>(
                                            "$apiOwnersPath/edit-owner",
                                            OwnerItem(ownerId, ownerName, ownerPhone),
                                            "put"
                                        )
                                        if (status == 201) alertTimer(message)
                                        closeAndFetch()
                                    } else {
                                        val (status, message) = commonRepository.postRequest<OwnerItem>(
                                            "$apiOwnersPath/create-owner",
                                            OwnerItem(null, ownerName, ownerPhone)
                                        )
                                        console.log(status)
                                        if (status == 201) alertTimer(message)
                                    }
                                    ownerId = 0
                                    ownerName = ""
                                    ownerPhone = ""
                                }
                            }
                        }
                    }
                ) {

                    formDiv(
                        "Nome", ownerName, InputType.Text, 98,
                        { event -> ownerName = event.value }, ownerNameError
                    )

                    formDiv(
                        "Telefone", ownerPhone, InputType.Text, 98,
                        { event -> ownerPhone = event.value }, ""
                    )

                    Div(attrs = { classes("min-submit-buttons") }) {
                        button("closeButton", "Fechar") {
                            closeAndFetch()
                        }
                        button("submitButton", btnText = submitBtnText, ButtonType.Submit)
                    }
                }
            }
        }
    }
}

