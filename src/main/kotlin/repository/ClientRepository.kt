package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.sessionStorage

class ClientRepository(private val httpClient: HttpClient) {

    private val token = sessionStorage.getItem("jwt_token") ?: ""

    suspend fun getClients(): List<ClientItem> {
        return httpClient.get("$apiClientsPath/all-clients") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

    suspend fun createClient(data: ClientItem): Int {
        return try {
            val response = httpClient.post("$apiClientsPath/create-client") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(data)
            }
            response.status.value
        } catch (e: Exception) {
            println("Error during POST: ${e.message}")
            400
        }
    }

    suspend fun editClient(data: ClientItem): Int{
        return try {
            val response = httpClient.put("$apiClientsPath/edit-client") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(data)
            }
            response.status.value
        } catch (e: Exception) {
            println("Error during POST: ${e.message}")
            400
        }
    }

    suspend fun deleteClient(clientId: Int): Int {
        val response = httpClient.delete("$apiClientsPath/delete-client/$clientId")
        return response.status.value
    }

}
