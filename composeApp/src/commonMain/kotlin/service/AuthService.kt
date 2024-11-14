package service

import android.util.Log
import data.Config
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
    private val baseUrl = "${Config.BASE_URL}/auth"

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
                Log.e("AuthService", "Error en login: Código de respuesta no exitoso - ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthService", "Excepción en login: ${e.message}", e)
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
                Log.e("AuthService", "Error en validateToken: Código de respuesta no exitoso - ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("AuthService", "Excepción en validateToken: ${e.message}", e)
            null
        }
    }
}