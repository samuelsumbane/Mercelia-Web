package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage
import kotlinx.browser.window
import kotlinx.html.MetaHttpEquiv.contentType

class BranchRepository(private val httpClient: HttpClient) {

    // all-brances, create-branch, update-branch, delete-branch

    val users = UserRepository(httpClient)
    private val token = sessionStorage.getItem("jwt_token") ?: ""


    suspend fun allBranches(): List<BranchItem> {
        return try {
            httpClient.get("$apiBranchesPath/all-branches") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body()
        } catch (e: Exception) {
            console.error("Erro ao buscar pedidos: ${e.message}")
            return emptyList()
        }
    }

    suspend fun updateBranch(updatedData: BranchItem): Int {
        return try {
            val response = httpClient.put("$apiBranchesPath/update-branch") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(updatedData)
            }
            println("Data updated successfully: $response")
            response.status.value
        } catch (e: Exception) {
//            println("Error during PUT: ${e.message}")
            400
        }
    }

    suspend fun createBranch(data: BranchItem): Pair<Int, String> {
        return try {
            val response = httpClient.post("$apiBranchesPath/create-branch") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(data)
            }
            Pair(response.status.value, response.bodyAsText())
        } catch (e: Exception) {
            println("Error during PUT: ${e.message}")
            Pair(400, "")
        }
    }

    suspend fun sysLocationId(): String {
        val locationId = localStorage.getItem("system_location")
        var systemLocation = ""
        if (locationId != null) {
            val branchItemData = allBranches().firstOrNull { it.id == locationId.toInt() }
            systemLocation = if (branchItemData != null) {
                locationId.toString()
            } else "404"
        } else {
            systemLocation = "405"
        }
        return systemLocation
    }


}

