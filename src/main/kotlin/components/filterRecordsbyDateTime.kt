package components

import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import kotlinx.coroutines.launch
import org.jetbrains.compose.web.attributes.ButtonType
import org.jetbrains.compose.web.attributes.InputType
import org.jetbrains.compose.web.attributes.onSubmit
import org.jetbrains.compose.web.dom.Br
import org.jetbrains.compose.web.dom.Div
import org.jetbrains.compose.web.dom.Form
import org.jetbrains.compose.web.dom.Option
import org.jetbrains.compose.web.dom.Text
import repository.OwnerItem
import view.state.AppState.filledField
import view.state.AppState.filledFields
import view.state.AppState.finalDateRequired
import view.state.AppState.finalTimeMessage
import view.state.AppState.initialDateRequired
import view.state.AppState.initialTimeMessage
import view.state.AppState.owner
import view.state.UiState.finalDate
import view.state.UiState.finalDateError
import view.state.UiState.finalTime
import view.state.UiState.initialDate
import view.state.UiState.initialDateError
import view.state.UiState.initialTime

@Composable
fun FilterRecordsByDateTime(
    thisModalState: String,
    ownerData: List<OwnerItem>,
    onCloseModal: () -> Unit,
    onOkaySubmitForm: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    minModal(thisModalState, "Selecionar Intervalo de Datas") {
        Form(
            attrs = {
                classes("modalform")
                onSubmit { event ->
                    event.preventDefault()
                    initialDateError = if (initialDate == "") initialDateRequired else ""
                    finalDateError = if (finalDate == "") finalDateRequired else ""

                    if (initialDate != "" && finalDate != "") {
                        if (initialTime == "" && finalTime == "") {
                            initialTime = "00:00"
                            finalTime = "23:59"
                            alert(
                                "info",
                                filledFields,
                                "$initialTimeMessage e a ${finalTimeMessage.lowercase()}"
                            )
                        } else if (initialTime == "") {
                            initialTime = "00:00"
                            alert("info", filledField, initialTimeMessage )
                        } else if (finalTime == "") {
                            finalTime = "23:59"
                            alert("info", filledField, finalTimeMessage)
                        }

                        //                            maxModalState = "open-max-modal"
                        onOkaySubmitForm()

                    } else if (initialDate.isBlank()) {
                        initialDateError = "Selecione a data inicial"
                    } else {
                        finalDateError = "Selecione a data final"
                    }
                }
            }
        ) {

            formDiv("Data Inicial", initialDate, inputType = InputType.Date, 0, oninput = { event ->
                initialDate = event.value
            }, initialDateError)

            formDiv(
                "Hora Inicial", initialTime, InputType.Time, 0,
                oninput = { event -> initialTime = event.value }, ""
            )

            Br()

            formDiv(
                "Data Final", finalDate, InputType.Date, 0,
                oninput = { event -> finalDate = event.value }, finalDateError
            )

            formDiv(
                "Hora Final", finalTime, InputType.Time, 0,
                oninput = { event -> finalTime = event.value }, ""
            )

            selectDiv(
                "Proprietário", "selectOwnerId",
                onOptionChange = { option ->
                    owner = if (option != null && option.toInt() != 0) {
                        option
                    } else {
                        "0"
                    }
                }
            ) {
                Option("0") { Text("Todos") }
                ownerData.forEach {
                    Option("${it.id}") { Text(it.name) }
                }
                if (owner.isBlank()) {
                    owner = "0" // Means none option selected ------->>
                }
            }

            Div(attrs = { classes("min-submit-buttons") }) {
                button("closeButton", "Fechar") { onCloseModal() }
                button("submitButton", "Submeter", ButtonType.Submit)
            }
        }
    }
}