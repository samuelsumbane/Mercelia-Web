package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.browser.sessionStorage

class FinanceRepository : ClassHttpClient() {

    // all-brances, create-branch, update-branch, delete-branch
    val users = UserRepository()
    private val token = sessionStorage.getItem("jwt_token") ?: ""


    suspend fun allPayables(): List<PayableItem> {
        return try {
            httpClient.get("$apiPayablesPath/all-payables") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body()
        } catch (e: Exception) {
            console.error("Erro ao buscar pedidos: ${e.message}")
            return emptyList()
        }
    }

    suspend fun allReceivables(): List<ReceivableItem> {
        return try {
            httpClient.post("$apiPayablesPath/all-receivables") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body()
        } catch (e: Exception) {
            console.error("Erro ao buscar pedidos: ${e.message}")
            return emptyList()
        }
    }

}

