package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText
import io.ktor.http.*
import kotlinx.browser.sessionStorage

class OwnersRepository : ClassHttpClient() {
    val token = sessionStorage.getItem("jwt_token") ?: ""

    suspend fun getOwners(): List<OwnerItem> {
        return httpClient.get("$apiOwnersPath/all-owners") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

    suspend fun deleteOwner(ownerId: Int): Int {
        val response = httpClient.delete("$apiOwnersPath/delete-owner/$ownerId")
        return response.status.value
    }
}
