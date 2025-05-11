package repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.sessionStorage

class ClientRepository : ClassHttpClient() {

    private val token = sessionStorage.getItem("jwt_token") ?: ""

    suspend fun getClients(): List<ClientItem> {
        return httpClient.get("$apiClientsPath/all") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }
}
