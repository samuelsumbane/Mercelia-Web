package repository

import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.http.*
import kotlinx.browser.sessionStorage

class CategoryRepository : ClassHttpClient() {
    val token = sessionStorage.getItem("jwt_token") ?: ""

    suspend fun getCategories(): List<CategoryItem> {
        return httpClient.get("$apiCategoriesPath/all-categories") {
            header(HttpHeaders.Authorization, "Bearer $token")
        }.body()
    }

    suspend fun deleteCategory(categoryId: Int): Int {
        val response = httpClient.delete("$apiCategoriesPath/delete-category/$categoryId")
        return response.status.value
    }
}
