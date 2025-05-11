package repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.sessionStorage

class NotificationRepository : ClassHttpClient() {

    private val token = sessionStorage.getItem("jwt_token") ?: ""
    suspend fun allNotifications(): List<NotificationItem> {
        return try {
            httpClient.get("$apiNotificationsPath/all") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body()
        } catch (e: Exception) {
            console.error("Erro ao buscar pedidos: ${e.message}")
            return emptyList()
        }
    }
}

