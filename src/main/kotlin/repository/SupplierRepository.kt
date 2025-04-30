package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.localStorage
import kotlinx.browser.sessionStorage

class SupplierRepository(private val httpClient: HttpClient) {

    private val token = sessionStorage.getItem("jwt_token") ?: ""

    suspend fun getSuppliers(): List<SupplierItem> {
        return httpClient.get("$apiSupplierPath/all-suppliers") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

    suspend fun createSupplier(data: SupplierItem): Int {
        return try {
            val response = httpClient.post("$apiSupplierPath/create-supplier") {
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

    suspend fun editSupplier(data: SupplierItem): Int {
        return try {
            val response = httpClient.put("$apiSupplierPath/edit-supplier") {
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



}