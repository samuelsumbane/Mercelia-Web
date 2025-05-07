package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.sessionStorage


//@Serializable
//sealed class OrderResult {
//    @Serializable data class Success(val orders: List<OrderItem>) : OrderResult()
//    @Serializable data class Empty(val message: String) : OrderResult()
//}

class SaleRepository : ClassHttpClient() {

    private val token = sessionStorage.getItem("jwt_token") ?: ""
    val users = UserRepository()

    suspend fun fetchOrders(): List<OrderItem> {
        return try {
            httpClient.get("$apiPath/orders") {
                header(HttpHeaders.Authorization, "Bearer $token")
            }.body()
        } catch (e: Exception) {
            console.error("Erro ao buscar pedidos: ${e.message}")
            return emptyList()
        }
    }

    suspend fun fetchOrderItems(id: String): List<OrderItemsItem> {
        return httpClient.get("$apiPath/order/order-items-by-id/$id") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }


//    suspend fun updateSale(updatedData: SalesControlDraft) {
//        try {
//            val response = httpClient.put("$apiPath/sale") {
//                contentType(ContentType.Application.Json)
//                setBody(updatedData)
//            }
//            println("Data updated successfully: $response")
//        } catch (e: Exception) {
//            println("Error during PUT: ${e.message}")
//        }
//    }
}