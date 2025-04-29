package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import io.ktor.client.statement.bodyAsText

class SettingsRepository(private val httpClient: HttpClient) {
    suspend fun getSettings(): List<SysConfigItem> {
        return httpClient.get("$apiPath/system_configurations/get-all").body()
    }

    suspend fun getPackageName(): Pair<Int, String> {
        val response = httpClient.get("$apiPath/system_configurations/package-name")
        return Pair(response.status.value, response.bodyAsText())
    }
}
