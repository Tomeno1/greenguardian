package data

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

private const val OPENAI_API_KEY = "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"


class OpenAIService(private val client: HttpClient) {

    private val baseUrl = "https://api.openai.com/v1/chat/completions"


    suspend fun getResponse(prompt: String): String {
        Log.d("OpenAIService", "Prompt: $prompt")
        val response: HttpResponse = client.post(baseUrl) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $OPENAI_API_KEY")
                contentType(ContentType.Application.Json)
            }
            setBody(
                OpenAIRequest(
                model = "gpt-3.5-turbo",
                messages = listOf(Message(role = "user", content = prompt))
            )
            )
        }
        val responseBodyText = response.bodyAsText()
        Log.d("OpenAIService", "Response Body: $responseBodyText")

        return try {
            val openAIResponse = Json { ignoreUnknownKeys = true }.decodeFromString<OpenAIResponse>(responseBodyText)
            openAIResponse.choices.firstOrNull()?.message?.content ?: "No response"
        } catch (e: Exception) {
            val errorResponse = Json { ignoreUnknownKeys = true }.decodeFromString<OpenAIErrorResponse>(responseBodyText)
            Log.d("OpenAIService", "Error: ${errorResponse.error.message}")
            "Error: ${errorResponse.error.message}"
        }
    }
}