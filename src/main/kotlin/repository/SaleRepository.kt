package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import io.ktor.http.encodedPath
import kotlinx.browser.localStorage


class SaleRepository(private val httpClient: HttpClient) {

    private val token = localStorage.getItem("jwt_token") ?: ""

    suspend fun fetchOrders(): List<OrderItem> {
        return httpClient.get("$apiPath/orders") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
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