package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.localStorage

class ProductRepository(private val httpClient: HttpClient) {

    private val token = localStorage.getItem("jwt_token") ?: ""

    suspend fun fetchProducts(): List<ProductItem> {
        return httpClient.get("$apiProductsPath/all-products") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }


//    suspend fun getAfiliateProducts(afId: Int): List<ProductItem> {
//        return httpClient.get("$apiPath/product/afiliate_products/${afId}").body()
//    }

    suspend fun createProduct(data: ProductItem): Int {
        return try {
            val response = httpClient.post("$apiProductsPath/create-product") {
                header(HttpHeaders.Authorization, "Bearer $token")
                contentType(ContentType.Application.Json)
                setBody(data)
            }
            response.status.value
        } catch (e: Exception) {
            println("Error during POST: ${e.message}")
            400
        }
    }

    suspend fun increaseProductStock(data: IncreaseProductStockDraft): Int {
        return try {
            val response = httpClient.put("$apiProductsPath/increase-stock") {
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

    suspend fun changeProductNameAndCategory(data: ProductNameAndCategory): Int {
        return try {
            val response = httpClient.put("$apiProductsPath/change-product-name-and-category") {
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

    suspend fun updateProductPrice(data: ChangeProductPriceDraft): Int {
        return try {
            val response = httpClient.put("$apiProductsPath/change-product-price") {
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