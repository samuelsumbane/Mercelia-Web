package repository

import io.ktor.client.*
import io.ktor.client.call.*
import io.ktor.client.request.*

class SettingsRepository(private val httpClient: HttpClient) {
    suspend fun getSettings(): List<SysConfigItem> {
        return httpClient.get("$apiPath/system_configurations/get-all").body()
    }
}
