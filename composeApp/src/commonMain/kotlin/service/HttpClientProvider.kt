package service

import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json

// Proveedor de configuración JSON para la serialización y deserialización
object JsonProvider {
    val json = Json {
        ignoreUnknownKeys = true // Ignora campos desconocidos en el JSON recibido del backend
        isLenient = true         // Permite una sintaxis JSON más relajada, útil para compatibilidad
        prettyPrint = true       // Formato legible para depuración en logs o consola
    }
}

// Proveedor del cliente HTTP configurado para las solicitudes de red
object HttpClientProvider {
    val client: HttpClient = HttpClient(CIO) { // Usa CIO como motor de cliente asíncrono
        install(ContentNegotiation) {           // Instala el plugin de negociación de contenido
            json(JsonProvider.json)             // Configura JSON como formato de serialización/deserialización
        }
    }
}
