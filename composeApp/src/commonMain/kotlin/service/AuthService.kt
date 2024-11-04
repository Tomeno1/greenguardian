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

class AuthService(private val client: HttpClient) {
    private val baseUrl = "http://192.168.1.98:8080/api/auth"

    suspend fun login(authUsuario: AuthUsuario): ResponseHttp? {
        return try {
            val response = client.post("$baseUrl/login") {
                contentType(ContentType.Application.Json)
                setBody(authUsuario)
            }
            if (response.status.isSuccess()) {
                val responseBodyText = response.bodyAsText()
                json.decodeFromString<ResponseHttp>(responseBodyText)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun validateToken(token: ResponseHttp): Usuario? {
        return try {
            val response = client.post("$baseUrl/user") {
                contentType(ContentType.Application.Json)
                setBody(token)
            }
            if (response.status.isSuccess()) {
                val responseBodyText = response.bodyAsText()
                json.decodeFromString<Usuario>(responseBodyText)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }
}