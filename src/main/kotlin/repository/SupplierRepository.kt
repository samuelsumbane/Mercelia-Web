package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage

class SupplierRepository() : ClassHttpClient() {

    private val token = sessionStorage.getItem("jwt_token") ?: ""

    suspend fun getSuppliers(): List<SupplierItem> {
        return httpClient.get("$apiSupplierPath/all-suppliers") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

}