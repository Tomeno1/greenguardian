package service

import android.util.Log
import data.Config
import io.ktor.client.HttpClient
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import model.MessageHorarioRiego
import model.MessageIrrigacion

// Servicio para interactuar con el backend para publicar mensajes MQTT
class MqttService(private val client: HttpClient) {
    private val baseUrl = "${Config.BASE_URL}/awsiot" // URL base de la API

    // Función para publicar un mensaje MQTT en un tema específico
    suspend fun publishMessage(topic: String, message: MessageIrrigacion): Result<String> {
        return try {
            val response: HttpResponse = client.post("$baseUrl/publish") {
                contentType(ContentType.Application.Json)
                parameter("topic", topic)
                setBody(message)
            }

            // Logs detallados de la respuesta del servidor
            Log.d("MqttService", "publishMessage - Código de estado: ${response.status.value}")
            Log.d("MqttService", "publishMessage - Cuerpo de respuesta: ${response.bodyAsText()}")

            if (response.status.isSuccess()) {
                Result.success(response.bodyAsText())
            } else {
                Result.failure(Exception("Error al publicar el mensaje en el tema $topic"))
            }
        } catch (e: Exception) {
            Log.e("MqttService", "Excepción en publishMessage: ${e.localizedMessage}")
            Result.failure(e)
        }
    }

    // Función para publicar un mensaje de riego MQTT en un tema específico
    suspend fun publishMessageRiego(topic: String, message: MessageHorarioRiego): Result<String> {
        return try {
            val response: HttpResponse = client.post("$baseUrl/publish/riego") {
                contentType(ContentType.Application.Json)
                parameter("topic", topic)
                setBody(message)
            }

            // Logs detallados de la respuesta del servidor
            Log.d("MqttService", "publishMessageRiego - Código de estado: ${response.status.value}")
            Log.d("MqttService", "publishMessageRiego - Cuerpo de respuesta: ${response.bodyAsText()}")

            if (response.status.isSuccess()) {
                Result.success(response.bodyAsText())
            } else {
                Result.failure(Exception("Error al publicar el mensaje en el tema $topic"))
            }
        } catch (e: Exception) {
            Log.e("MqttService", "Excepción en publishMessageRiego: ${e.localizedMessage}")
            Result.failure(e)
        }
    }
}
