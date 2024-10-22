package data

import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import model.MessageMqtt

class MqttService(private val client: HttpClient) {
    private val baseUrl = "http://192.168.1.98:8080/api/awsiot"

    suspend fun publishMessage(topic: String, message: MessageMqtt): Result<String> {
        return try {
            val response: HttpResponse = client.post("$baseUrl/publish") {
                contentType(ContentType.Application.Json)
                parameter("topic", topic)
                setBody(message)
            }
            if (response.status.isSuccess()) {
                Result.success(response.bodyAsText())  // Retorna el cuerpo de la respuesta como resultado de éxito
            } else {
                Result.failure(Exception("Error al publicar el mensaje en el tema $topic"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)  // Manejo del error en caso de excepción
        }
    }
}
