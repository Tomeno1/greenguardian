package service

import service.JsonProvider.json
import io.ktor.client.HttpClient
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import model.AuthUsuario
import model.ResponseHttp
import model.Usuario

// Servicio de autenticación que usa el cliente HTTP de Ktor para realizar solicitudes al backend
class AuthService(private val client: HttpClient) {
    private val baseUrl = "http://192.168.1.98:8080/api/auth" // URL base para el servicio de autenticación

    // Función para iniciar sesión, tomando un objeto AuthUsuario y devolviendo un ResponseHttp en caso de éxito
    suspend fun login(authUsuario: AuthUsuario): ResponseHttp? {
        return try {
            val response = client.post("$baseUrl/login") { // Realiza una solicitud POST a la URL de login
                contentType(ContentType.Application.Json)  // Define el tipo de contenido como JSON
                setBody(authUsuario)  // Configura el cuerpo de la solicitud con los datos de usuario para login
            }
            if (response.status.isSuccess()) {  // Verifica si la respuesta fue exitosa
                val responseBodyText = response.bodyAsText()  // Extrae el cuerpo de la respuesta en formato de texto
                json.decodeFromString<ResponseHttp>(responseBodyText)  // Decodifica el texto a un objeto ResponseHttp
            } else {
                null // Si la respuesta no es exitosa, devuelve null
            }
        } catch (e: Exception) {
            null // Si ocurre algún error durante la solicitud, devuelve null
        }
    }

    // Función para validar un token, que toma un ResponseHttp y devuelve un objeto Usuario si el token es válido
    suspend fun validateToken(token: ResponseHttp): Usuario? {
        return try {
            val response = client.post("$baseUrl/user") { // Realiza una solicitud POST a la URL de validación de usuario
                contentType(ContentType.Application.Json)  // Define el tipo de contenido como JSON
                setBody(token)  // Configura el cuerpo de la solicitud con el token de autenticación
            }
            if (response.status.isSuccess()) { // Verifica si la respuesta fue exitosa
                val responseBodyText = response.bodyAsText() // Extrae el cuerpo de la respuesta en texto
                json.decodeFromString<Usuario>(responseBodyText) // Decodifica el texto en un objeto Usuario
            } else {
                null // Si la respuesta no es exitosa, devuelve null
            }
        } catch (e: Exception) {
            null // Si ocurre algún error durante la solicitud, devuelve null
        }
    }
}
