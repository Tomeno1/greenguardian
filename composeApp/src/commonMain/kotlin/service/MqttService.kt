package service

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
import model.MessageMqtt

// Servicio para interactuar con el backend para publicar mensajes MQTT
class MqttService(private val client: HttpClient) {
    private val baseUrl = "${Config.BASE_URL}/awsiot" // URL base de la API

    // Función para publicar un mensaje MQTT en un tema específico
    suspend fun publishMessage(topic: String, message: MessageMqtt): Result<String> {
        return try {
            // Realiza una solicitud POST al endpoint de publicación MQTT
            val response: HttpResponse = client.post("$baseUrl/publish") {
                contentType(ContentType.Application.Json) // Establece el tipo de contenido como JSON
                parameter("topic", topic) // Agrega el tema como parámetro en la URL
                setBody(message) // Configura el mensaje en el cuerpo de la solicitud
            }

            // Verifica si la respuesta es exitosa
            if (response.status.isSuccess()) {
                Result.success(response.bodyAsText())  // Retorna el cuerpo de la respuesta en caso de éxito
            } else {
                // Devuelve un error si la publicación falla
                Result.failure(Exception("Error al publicar el mensaje en el tema $topic"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)  // Manejo de errores en caso de excepción
        }
    }
}
