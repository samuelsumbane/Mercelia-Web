package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.localStorage

class CategoryRepository(private val httpClient: HttpClient) {
    val token = localStorage.getItem("jwt_token") ?: ""

    suspend fun getCategories(): List<CategoryItem> {
        return httpClient.get("$apiCategoriesPath/all-categories") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

    suspend fun createCategory(data: CategoryItem): Int {
        return try {
            val response = httpClient.post("$apiCategoriesPath/create-category") {
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

    suspend fun editCategory(data: CategoryItem): Int {
        return try {
            val response = httpClient.put("$apiCategoriesPath/update-category") {
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


//    suspend fun fetchEachAfiliateData(id: Int): AfiliateItem {
//        return httpClient.get("$apiPath/afiliate/get_afiliate_by_id/$id").body()
//    }
//