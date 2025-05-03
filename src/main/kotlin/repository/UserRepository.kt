package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage
import kotlinx.browser.window
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive
import org.w3c.dom.get
import org.w3c.dom.set


class UserRepository(private val httpClient: HttpClient) {
    private val token = sessionStorage.getItem("jwt_token") ?: ""

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
        val response = httpClient.get("$apiPath/user/check-session") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }
        println(response.status)
        return if (response.status == HttpStatusCode.Accepted) {
            val jsonResponse = Json.parseToJsonElement(response.bodyAsText()) as JsonObject
            val userid = jsonResponse["userid"]?.jsonPrimitive?.content?.toInt()
            val username = jsonResponse["username"]?.jsonPrimitive?.content
            val userrole = jsonResponse["userrole"]?.jsonPrimitive?.content
            LoggedUserDC(true, userid!!, username!!, userrole!!)
        } else {
            LoggedUserDC(false, 0, "", "")
        }
    }


    suspend fun login(data: LoginRequest): Pair<Boolean, String> {
        val encodedRegion = "Africa/Harare"
        val response = httpClient.post("$apiPath/user/login?timezoneid=$encodedRegion") {
            contentType(ContentType.Application.Json)
            setBody(data)
        }

        if (response.status == HttpStatusCode.Accepted) {
            val jsonResponse = Json.parseToJsonElement(response.bodyAsText()) as JsonObject
            val token = jsonResponse["token"]?.jsonPrimitive?.content
            val userRole = jsonResponse["userRole"]?.jsonPrimitive?.content

            if (token != null) {
                sessionStorage.setItem("jwt_token", token)
                return Pair(true, userRole!!)
            }
        }

        return Pair(false, "")
    }

    suspend fun logout(): Pair<Int, String> {
        return try {
            val response = httpClient.post("$apiPath/user/logout") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
//                setBody(data)
            }
            Pair(response.status.value, response.bodyAsText())
        } catch (e: Exception) {
            Pair(-1, "Error: ${e.message}")
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

    suspend fun updateUserPassword(data: PasswordDraft): Pair<Int, String> {
        val response = httpClient.post("$apiPath/user/update-user-password") {
            header("Authorization", "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(data)
        }
        return Pair(response.status.value, response.bodyAsText())
    }

    suspend fun verifyPassord(data: VerifyPasswordDC): Pair<Int, String> {
        val response = httpClient.post("$apiPath/user/verify-password") {
            header(HttpHeaders.Authorization, "Bearer $token")
            contentType(ContentType.Application.Json)
            setBody(data)
        }
        return Pair(response.status.value, response.bodyAsText())
    }

//    suspend fun <T: Number> basicPostRequest(data: T, path: String): Pair<Int, String> {
//        val response = httpClient.post(path) {
//            header(HttpHeaders.Authorization, "Bearer $token")
//            contentType(ContentType.Application.Json)
//            setBody(data)
//        }
//        return Pair(response.status.value, response.bodyAsText())
//    }




}