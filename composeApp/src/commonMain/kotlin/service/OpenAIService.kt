package service

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import kotlinx.serialization.json.Json
import model.Message
import model.OpenAIErrorResponse
import model.OpenAIRequest
import model.OpenAIResponse

// Clave de API para autenticar solicitudes a OpenAI (debería almacenarse en un lugar seguro)
private const val OPENAI_API_KEY = "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"

// Servicio que interactúa con la API de OpenAI
class OpenAIService(private val client: HttpClient) {

    private val baseUrl = "https://api.openai.com/v1/chat/completions" // URL del endpoint de OpenAI

    // Función para obtener la respuesta de OpenAI a un mensaje de usuario
    suspend fun getResponse(prompt: String): String {
        Log.d("OpenAIService", "Prompt: $prompt") // Log para depuración

        // Realiza una solicitud POST a la API de OpenAI
        val response: HttpResponse = client.post(baseUrl) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $OPENAI_API_KEY") // Autenticación con la API Key
                contentType(ContentType.Application.Json) // Establece el tipo de contenido como JSON
            }
            setBody(
                OpenAIRequest(
                    model = "gpt-3.5-turbo", // Modelo GPT a utilizar
                    messages = listOf(Message(role = "user", content = prompt)) // Mensaje enviado por el usuario
                )
            )
        }

        // Obtener el cuerpo de la respuesta en formato de texto
        val responseBodyText = response.bodyAsText()
        Log.d("OpenAIService", "Response Body: $responseBodyText") // Log para depuración

        // Intenta decodificar la respuesta de OpenAI
        return try {
            // Decodifica la respuesta JSON en un objeto OpenAIResponse
            val openAIResponse = Json { ignoreUnknownKeys = true }.decodeFromString<OpenAIResponse>(responseBodyText)
            openAIResponse.choices.firstOrNull()?.message?.content ?: "No response" // Retorna el contenido de la respuesta o un mensaje por defecto
        } catch (e: Exception) {
            // En caso de error, intenta decodificarlo como un OpenAIErrorResponse
            val errorResponse = Json { ignoreUnknownKeys = true }.decodeFromString<OpenAIErrorResponse>(responseBodyText)
            Log.d("OpenAIService", "Error: ${errorResponse.error.message}") // Log de error
            "Error: ${errorResponse.error.message}" // Retorna el mensaje de error
        }
    }
}
