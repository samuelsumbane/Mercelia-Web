package view.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue


object UiState {
    var modalTitle by mutableStateOf("")
    var modalState by mutableStateOf("closed") //closed = "" --------->>
    var submitBtnText by mutableStateOf("Submeter")
    var maxModalState by mutableStateOf("closed") //closed = "" --------->>
//    var maxModalState by remember { mutableStateOf("open-max-modal") } //closed =
    var maySendData by mutableStateOf(false)

    var initialDate by mutableStateOf("")
    var initialTime by mutableStateOf("")
    var finalDate by mutableStateOf("")
    var finalTime by mutableStateOf("")
    var initialDateError by mutableStateOf("")
    var finalDateError by mutableStateOf("")
    var description by mutableStateOf("")
    var paymentForm by  mutableStateOf("")
    var showThemeModeChooserDiv by mutableStateOf(false)
    var showPerfilDiv by mutableStateOf(false)
    var actualTheme by mutableStateOf("")
    var currentActualThemeName by mutableStateOf("")




}