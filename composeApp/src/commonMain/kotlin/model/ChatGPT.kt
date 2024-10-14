package model

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
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json

private const val OPENAI_API_KEY = "sk-proj-1J2XVhfJPrKWONx84vSeT3BlbkFJ26vr4VyzqMXV5AFjAeib"

@Serializable
data class OpenAIRequest(
    val model: String,
    val messages: List<Message>
)

@Serializable
data class Message(
    val role: String,
    val content: String
)

@Serializable
data class OpenAIResponse(
    val choices: List<Choice> = emptyList()
)

@Serializable
data class Choice(
    val message: Message
)

@Serializable
data class OpenAIError(
    val message: String,
    val type: String,
    val param: String?,
    val code: String?
)

@Serializable
data class OpenAIErrorResponse(
    val error: OpenAIError
)

class OpenAIService(private val client: HttpClient) {

    private val baseUrl = "https://api.openai.com/v1/chat/completions"

    suspend fun getResponse(prompt: String): String {
        Log.d("OpenAIService", "Prompt: $prompt")
        val response: HttpResponse = client.post(baseUrl) {
            headers {
                append(HttpHeaders.Authorization, "Bearer $OPENAI_API_KEY")
                contentType(ContentType.Application.Json)
            }
            setBody(OpenAIRequest(
                model = "gpt-3.5-turbo",
                messages = listOf(Message(role = "user", content = prompt))
            ))
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