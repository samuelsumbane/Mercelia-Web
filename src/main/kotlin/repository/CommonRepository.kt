package repository

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.browser.sessionStorage
import kotlinx.serialization.json.Json

class CommonRepository : ClassHttpClient() {
    val token = sessionStorage.getItem("jwt_token") ?: ""

    suspend inline fun <reified T : Any> postRequest(
        url: String, data: T,
        method: String = "post"
    ): Pair<Int, String> {
        return try {
            val response = if (method == "post") httpClient.post(url) else httpClient.put(url) {
                contentType(ContentType.Application.Json)
                header(HttpHeaders.Authorization, "Bearer $token")
                setBody(data)
            }
            Pair(response.status.value, response.bodyAsText())
        } catch (e: Exception) {
            println("Error during POST: ${e.message}")
            Pair(400, "")
        }
    }
}