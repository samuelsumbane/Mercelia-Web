package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.plugins.*
import io.ktor.client.request.*
import io.ktor.client.statement.*
import io.ktor.http.*
import io.ktor.http.encodedPath
import kotlinx.browser.localStorage
import kotlinx.browser.window
import kotlinx.serialization.Serializable
import org.w3c.dom.get
import org.w3c.dom.set

//@Serializable
//sealed class OrderResult {
//    @Serializable data class Success(val orders: List<OrderItem>) : OrderResult()
//    @Serializable data class Empty(val message: String) : OrderResult()
//}

class SaleRepository(private val httpClient: HttpClient) {

    private val token = localStorage.getItem("jwt_token") ?: ""
    val users = UserRepository(httpClient)

    suspend fun fetchOrders(): List<OrderItem> {
        try {
            val response: HttpResponse? = users.apiRequest("$apiPath/orders")

            if (response?.status == HttpStatusCode.Unauthorized) {
                // 🔥 Se o usuário não estiver autenticado, remove os tokens e redireciona
                localStorage.removeItem("token")
                localStorage.removeItem("refreshToken")
                console.log("Sessão expirada, redirecionando para login...")
                window.location.href = "/"
                return emptyList()
            }
            return response!!.body()

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

//
    suspend fun saleProduct(data: SaleItem): Int {
        return try {
            val response = httpClient.post("$apiPath/order/sale_products") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(data) // Enviar o objeto como JSON
            }
            response.status.value
        } catch (e: Exception) {
            println("Error during POST: ${e.message}")
            400
        }
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