package view.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

object AppState {
    var isLoading by mutableStateOf(true)
    var error by mutableStateOf<String?>(null)
    var sysPackage by mutableStateOf("")
}