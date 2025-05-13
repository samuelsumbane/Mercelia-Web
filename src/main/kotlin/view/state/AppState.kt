package view.state

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import repository.NotificationItem

object AppState {
    var isLoading by mutableStateOf(true)
    var error by mutableStateOf<String?>(null)
    var sysPackage by mutableStateOf("")
    var allNotifications by mutableStateOf(listOf<NotificationItem>())
    var sysLocationId by mutableStateOf("")
    var branchDeffered by  mutableStateOf("")

}