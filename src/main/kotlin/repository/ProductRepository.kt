package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.sessionStorage

class ProductRepository : ClassHttpClient() {

    private val token = sessionStorage.getItem("jwt_token") ?: ""

    suspend fun fetchProducts(): List<ProductItem> {
        return httpClient.get("$apiProductsPath/all-products") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

//    suspend fun getAfiliateProducts(afId: Int): List<ProductItem> {
//        return httpClient.get("$apiPath/product/afiliate_products/${afId}").body()
//    }

    suspend fun updateProductPrice(data: ChangeProductPriceDraft): Int {
        return try {
            val response = httpClient.put() {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(data) // Enviar o objeto como JSON
            }
            console.log(response.status.value)
            response.status.value
        } catch (e: Exception) {
            println("Error during POST: ${e.message}")
            400
        }
    }


}