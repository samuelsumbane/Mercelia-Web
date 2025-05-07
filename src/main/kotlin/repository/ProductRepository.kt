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
}