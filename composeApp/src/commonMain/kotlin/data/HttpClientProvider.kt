package data

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json


object JsonProvider {
    val json = Json { ignoreUnknownKeys = true }
}

object HttpClientProvider {
    val client: HttpClient = HttpClient(CIO) {
        install(ContentNegotiation) {
            json(JsonProvider.json)
        }
    }
}