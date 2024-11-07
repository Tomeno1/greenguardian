package service

import android.util.Log
import service.JsonProvider.json
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import model.EstanqueByUsuarioResponse
import model.PromedioEstanques
import model.Usuario

// Servicio que permite realizar operaciones CRUD sobre usuarios y obtener información de estanques relacionados
class UserService(private val client: HttpClient) {
    private val baseUrl = "http://192.168.1.98:8080/api/usuarios" // URL base para el endpoint de usuarios

    // Obtener un usuario específico por ID con autenticación
    suspend fun getUser(token: String, userId: Long): Usuario? {
        return try {
            val response: HttpResponse = client.get("$baseUrl/$userId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token") // Añadir token de autenticación
                    accept(ContentType.Application.Json)               // Espera JSON como respuesta
                }
            }
            if (response.status.isSuccess()) {
                val responseBodyText = response.bodyAsText()
                json.decodeFromString<Usuario>(responseBodyText) // Deserializar a un objeto Usuario
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Actualizar un usuario por ID usando un token de autenticación
    suspend fun updateUser(token: String, usuario: Usuario): Boolean {
        Log.d("UserService", "Iniciando updateUser para usuario: ${usuario.idUsuario}")
        return try {
            val response: HttpResponse = client.put("$baseUrl/${usuario.idUsuario}") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token") // Añadir token
                    contentType(ContentType.Application.Json)           // Definir JSON como tipo de contenido
                }
                setBody(usuario) // Enviar datos del usuario como cuerpo de la solicitud
            }
            val success = response.status.isSuccess()
            Log.d("UserService", "updateUser ${if (success) "éxito" else "fallo"} con status: ${response.status}")
            success
        } catch (e: Exception) {
            Log.e("UserService", "Excepción en updateUser: ${e.message}")
            false
        }
    }

    // Eliminar un usuario por ID usando token de autenticación
    suspend fun deleteUser(token: String, userId: Long): Boolean {
        Log.d("UserService", "Iniciando deleteUser para userId: $userId")
        return try {
            val response: HttpResponse = client.delete("$baseUrl/$userId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            val success = response.status.isSuccess()
            Log.d("UserService", "deleteUser ${if (success) "éxito" else "fallo"} con status: ${response.status}")
            success
        } catch (e: Exception) {
            Log.e("UserService", "Excepción en deleteUser: ${e.message}")
            false
        }
    }

    // Obtener estanques asociados a un usuario específico por ID
    suspend fun getEstanquesByUsuario(token: String, userId: Long): EstanqueByUsuarioResponse? {
        Log.d("UserService", "Iniciando getEstanquesByUsuario para userId: $userId")
        return try {
            val response: HttpResponse = client.get("$baseUrl/estanque-by-idUsuario/$userId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    accept(ContentType.Application.Json)
                }
            }
            if (response.status.isSuccess()) {
                val responseBodyText = response.bodyAsText()
                Log.d("UserService", "getEstanquesByUsuario éxito: $responseBodyText")
                json.decodeFromString<EstanqueByUsuarioResponse>(responseBodyText) // Decodifica a EstanqueByUsuarioResponse
            } else {
                Log.w("UserService", "getEstanquesByUsuario fallo con status: ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserService", "Excepción en getEstanquesByUsuario: ${e.message}")
            null
        }
    }

    // Obtener el promedio de medidas de estanques asociados a un usuario específico
    suspend fun getPromedioEstanques(token: String, userId: Long): PromedioEstanques? {
        Log.d("UserService", "Iniciando getPromedioEstanques para userId: $userId")
        return try {
            val response: HttpResponse = client.get("$baseUrl/promedio-estanques/$userId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    accept(ContentType.Application.Json)
                }
            }
            if (response.status.isSuccess()) {
                val responseBodyText = response.bodyAsText()
                Log.d("UserService", "getPromedioEstanques éxito: $responseBodyText")
                json.decodeFromString<PromedioEstanques>(responseBodyText) // Deserializar a PromedioEstanques
            } else {
                Log.w("UserService", "getPromedioEstanques fallo con status: ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("UserService", "Excepción en getPromedioEstanques: ${e.message}")
            null
        }
    }
}
