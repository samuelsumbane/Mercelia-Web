package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.w3c.dom.get
import org.w3c.dom.set


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

    suspend fun checkSession(): LoggedUserDC? {
        val response = apiRequest("$apiPath/user/check-session")
        return if (response == null) {
            localStorage.removeItem("jwt_token")
            localStorage.removeItem("refreshToken")
            console.log("Sessão expirada, redirecionando para login...")
            // 🔥 Redireciona para a página de login
//            window.location.href = "/"
            null
        } else {
            if (response.status == HttpStatusCode.Accepted) {
                val jsonResponse = Json.parseToJsonElement(response.bodyAsText()) as JsonObject
                val userid = jsonResponse["userid"]?.jsonPrimitive?.content?.toInt()
                val username = jsonResponse["username"]?.jsonPrimitive?.content
                val userrole = jsonResponse["userrole"]?.jsonPrimitive?.content
                LoggedUserDC(true, userid!!, username!!, userrole!!)
            } else {
                LoggedUserDC(false, 0, "", "")
            }
        }


//        return response?.status == HttpStatusCode.Accepted
    }


    suspend fun refreshAccessToken(): String? {
        val refreshToken = localStorage["refreshToken"]

        return try {
            val response: HttpResponse = httpClient.post("$apiPath/refresh-token") {
                contentType(ContentType.Application.Json)
                setBody(mapOf("refreshToken" to refreshToken))
            }
            val jsonResponse = response.body<Map<String, String>>()
            jsonResponse["jwt_token"]
        } catch (e: Exception) {
            null
        }
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

            if (token != null) {
                localStorage.setItem("jwt_token", token)
//                val us = response.bodyAsText()
//                println(us)
                return Pair(true, userId!!)
            }
        }

        return Pair(false, 0)
    }

    fun logout() {
        localStorage.removeItem("jwt_token")
        localStorage.removeItem("refreshToken")
    }



    suspend fun apiRequest(apitpath: String): HttpResponse? {
        val token = localStorage["jwt_token"] // 🔥 Pegando o token armazenado

        return try {
            httpClient.get(apitpath) {
                header("Authorization", "Bearer $token")
            }
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.Unauthorized) {
                // 🔥 Token expirou, tenta renovar
                val newToken = refreshAccessToken()
                if (newToken != null) {
                    localStorage["jwt_token"] = newToken
                    return apiRequest(apitpath) // 🔄 Tenta de novo com o novo token
                }
            }
            null
        }
    }

    suspend fun changeUserStatus(data: ChangeStatusDC): Pair<Int, String> {
        val response = httpClient.post("$apiPath/user/change-status") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(data)
        }
        return Pair(response.status.value, response.bodyAsText())
    }

    suspend fun changeUserRole(data: ChangeRoleDC): Pair<Int, String> {
        val response = httpClient.post("$apiPath/user/change-role") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(data)
        }
        return Pair(response.status.value, response.bodyAsText())
    }


}