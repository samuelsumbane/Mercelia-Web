package repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage

class BranchRepository : ClassHttpClient() {

    // all-brances, create-branch, update-branch, delete-branch

    val users = UserRepository()
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

