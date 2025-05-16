package view.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import repository.NotificationItem

object AppState {
    var isLoading by mutableStateOf(true)
    var error by mutableStateOf<String?>(null)
    var sysPackage by mutableStateOf("")
    var allNotifications by mutableStateOf(listOf<NotificationItem>())
    var sysLocationId by mutableStateOf("")
    var branchDeffered by  mutableStateOf("")
    var userName by mutableStateOf("")
    var owner by mutableStateOf("")

    var initialTimeMessage = "A hora inicial será 00:00."
    var finalTimeMessage = "A hora final será 23:59."
    const val filledField = "Campos Preenchido"
    const val filledFields = "Campos Preenchidos"
    const val initialDateRequired = "A data inicial é obrigatória"
    const val finalDateRequired = "A data final é obrigatória"
}