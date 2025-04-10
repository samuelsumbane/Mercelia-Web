package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.browser.localStorage
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive


class UserRepository(private val httpClient: HttpClient) {
    private val token = localStorage.getItem("jwt_token") ?: ""

    suspend fun fetchUsers(): List<UserItem> {
        return httpClient.get("$apiPath/users") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }


    suspend fun getUserStatus(): Pair<Int, Int> {
        return httpClient.get("$apiPath/user/usersStatus").body()
    }

    suspend fun getUserById(id: Int): UserItem {
        return httpClient.get("$apiPath/user/by-user-id/$id") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

    suspend fun createUser(data: UserItemDraft): Pair<Int, String> {
        return try {
            val response = httpClient.post("$apiPath/user/create_user") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(data)
            }
            Pair(response.status.value, response.bodyAsText())
        } catch (e: Exception) {
            Pair(-1, "Error: ${e.message}")
        }
    }


    suspend fun updateUser(data: UserItemDraft): Pair<Int, String> {
        return try {
            val response = httpClient.post("$apiPath/user/") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(data)
            }
            Pair(response.status.value, response.bodyAsText())
        } catch (e: Exception) {
            Pair(-1, "Error: ${e.message}")
        }
    }

    suspend fun checkSession(): Boolean {
        val response = httpClient.get("$apiPath/user/check-session") {
            header(HttpHeaders.Authorization, "Bearer $token") // 🔥 Envia o token JWT
        }
        return response.status == HttpStatusCode.Accepted
    }

    suspend fun login(data: LoginRequest): Pair<Boolean, Int> {
        val encodedRegion = "Africa/Harare"
        val response = httpClient.post("$apiPath/user/login?timezoneid=$encodedRegion") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }

        if (response.status == HttpStatusCode.Accepted) {
            val jsonResponse = Json.parseToJsonElement(response.bodyAsText()) as JsonObject
            val token = jsonResponse["token"]?.jsonPrimitive?.content
            val userId = jsonResponse["userId"]?.jsonPrimitive?.content?.toInt()
//            console.log(data)

            if (token != null) {
                localStorage.setItem("jwt_token", token)
//                val us = response.bodyAsText()
//                println(us)
                return Pair(true, userId!!)
            }
        }

        return Pair(false, 0)
    }


}