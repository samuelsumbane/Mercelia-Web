package view.Afiliates


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
import org.jetbrains.compose.web.dom.*
import repository.*


@Composable
fun OwnersPage(userRole: String, sysPackage: String) {

    val owners = OwnersRepository()
    val commonRepository = CommonRepository()
    var ownerData by remember { mutableStateOf<List<OwnerItem>?>(null) }

    var error by remember { mutableStateOf<String?>(null) }
    val coroutineScope = rememberCoroutineScope()
    var modalTitle by remember { mutableStateOf("") }
    var modalState by remember { mutableStateOf("closed") } //closed = "" --------->>
//    var modalState by remember { mutableStateOf("open-min-modal") } //closed = "" --------->>
    var ownerId by remember { mutableStateOf(0) }
    var ownerName by remember { mutableStateOf("") }
    var ownerNameError by remember { mutableStateOf("") }
    var ownerPhone by remember { mutableStateOf("") }
    var submitBtnText by remember { mutableStateOf("Submeter") }
    val router = Router.current

    LaunchedEffect(Unit) {
        if (userRole != Role.V.desc) {
            try {
                ownerData = owners.getOwners()
            } catch (e: Exception) {
                error = "Error: ${e.message}"
            }
        }
    }

    NormalPage(
        showBackButton = true,
        onBackFunc = { router.navigate("/basicPartnersPage") },
        title = "Proprietários", pageActivePath = "sidebar-btn-partners",
        sysPackage = sysPackage,
        userRole = userRole, hasMain = true, hasNavBar = true, navButtons = {
            button("btnSolid", "+ Proprietário") {
                modalTitle = "Adicionar Proprietário"
                modalState = "open-min-modal"
                submitBtnText = "Submeter"
                //
                ownerNameError = ""
            }
        }) {

        if (ownerData != null) {
            if (ownerData!!.isEmpty()) {
                Div(attrs = { classes("centerDiv") }) {
                    Text("Nenhum proprietário encontrado.")
                }
            }
            ownerData!!.forEach { item ->
                cardWG(title = "", cardButtons = { cardButtons(
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
//                            val status = owners.deleteClient(item.id!!)
//                            when (status) {
//                                200 -> {
//                                    alertTimer("Proprietário deletado com sucesso.")
//                                    ownerData = owners.getClients()
//                                }
//                                404 -> alert("error", "Proprietário não encontrado.", "")
//                                406 -> alert("warning", "Delete não aceite.", "O cliente já tem dados no sistema")
//                            }
                        }
                    }
                ) }) {
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

                        if (ownerName.isNotBlank()) {
                            coroutineScope.launch {
                                if (ownerId != 0) {
                                    val (status, message) = commonRepository.postRequest<OwnerItem>("$apiOwnersPath/edit-owner", OwnerItem(ownerId, ownerName, ownerPhone), "put")

                                    if (status == 201) alertTimer("Proprietário actualizado com sucesso.")
                                    modalState = "closed"
                                } else {
                                    val (status, message) = commonRepository.postRequest<OwnerItem>("$apiOwnersPath/create-owner", OwnerItem(null, ownerName, ownerPhone))
//                                    val status = owners.createOwner(OwnerItem(null, ownerName, ownerPhone))
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

                formDiv("Nome", ownerName, InputType.Text, 98,
                    { event -> ownerName = event.value}, ownerNameError
                )

                formDiv("Telefone", ownerPhone, InputType.Text, 98,
                    { event -> ownerPhone = event.value}, ""
                )

                Div(attrs = { classes("min-submit-buttons") }) {
                    button("closeButton", "Fechar") {
                        modalState = "closed"
                        coroutineScope.launch { ownerData = owners.getOwners() }
                    }
                    button("submitButton", btnText = submitBtnText, ButtonType.Submit)
                }
            }
        }
    }

}

