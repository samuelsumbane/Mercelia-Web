package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import kotlinx.browser.sessionStorage

class FinanceRepository(private val httpClient: HttpClient) {

    // all-brances, create-branch, update-branch, delete-branch
    val users = UserRepository(httpClient)
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

//    suspend fun updateBranch(updatedData: BranchItem): Int {
//        return try {
//            val response = httpClient.put("$apiBranchesPath/update-branch") {
//                header(HttpHeaders.Authorization, "Bearer $token")
//                contentType(ContentType.Application.Json)
//                setBody(updatedData)
//            }
//            println("Data updated successfully: $response")
//            response.status.value
//        } catch (e: Exception) {
////            println("Error during PUT: ${e.message}")
//            400
//        }
//    }

    suspend fun createPayable(data: PayableDraft): Pair<Int, String> {
        return try {
            val response = httpClient.post("$apiPayablesPath/create-payable") {
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

    suspend fun payABill(data: IdAndStatus): Pair<Int, String> {
        return try {
            val response = httpClient.put("$apiPayablesPath/pay-account") {
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

    suspend fun receiveBillPayment(data: IdAndStatus): Pair<Int, String> {
        return try {
            val response = httpClient.put("$apiReceivablesPath/receive-account-payment") {
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

//


//    idAndStatus

    suspend fun createReceivable(data: ReceivableDraft): Pair<Int, String> {
        return try {
            val response = httpClient.post("$apiReceivablesPath/create-receivable") {
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

//    suspend fun sysLocationId(): String {
//        val locationId = localStorage.getItem("system_location")
//        var systemLocation = ""
//        if (locationId != null) {
//            val branchItemData = allBranches().firstOrNull { it.id == locationId.toInt() }
//            systemLocation = if (branchItemData != null) {
//                locationId.toString()
//            } else "404"
//        } else {
//            systemLocation = "405"
//        }
//        return systemLocation
//    }


}

