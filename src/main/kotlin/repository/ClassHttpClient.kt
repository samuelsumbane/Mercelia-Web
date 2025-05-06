package repository

import io.ktor.client.HttpClient
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

open class ClassHttpClient {
    val httpClient = HttpClient {
        install(ContentNegotiation) {
            json(Json { isLenient = true })
        }
    }
}