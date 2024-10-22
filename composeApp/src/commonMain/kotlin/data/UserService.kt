package data

import data.JsonProvider.json
import io.ktor.client.HttpClient
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
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

class UserService(private val client: HttpClient) {
    private val baseUrl = "http://192.168.1.98:8080/api/usuarios"

    // Obtener un usuario por ID con token de autenticación
    suspend fun getUser(token: String, userId: Long): Usuario? {
        return try {
            val response: HttpResponse = client.get("$baseUrl/$userId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    accept(ContentType.Application.Json)
                }
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

    // Actualizar un usuario por ID con token de autenticación
    suspend fun updateUser(token: String, usuario: Usuario): Boolean {
        return try {
            val response: HttpResponse = client.put("$baseUrl/${usuario.idUsuario}") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                }
                setBody(usuario)
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }

    // Eliminar un usuario por ID con token de autenticación
    suspend fun deleteUser(token: String, userId: Long): Boolean {
        return try {
            val response: HttpResponse = client.delete("$baseUrl/$userId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            false
        }
    }



    // Obtener los estanques de un usuario por ID con token de autenticación
    suspend fun getEstanquesByUsuario(token: String, userId: Long): EstanqueByUsuarioResponse? {
        return try {
            val response: HttpResponse = client.get("$baseUrl/estanque-by-idUsuario/$userId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    accept(ContentType.Application.Json)
                }
            }
            if (response.status.isSuccess()) {
                val responseBodyText = response.bodyAsText()
                json.decodeFromString<EstanqueByUsuarioResponse>(responseBodyText)
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getPromedioEstanques(token: String, userId: Long): PromedioEstanques? {
        return try {
            val response: HttpResponse = client.get("$baseUrl/promedio-estanques/$userId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    accept(ContentType.Application.Json)
                }
            }

            if (response.status.isSuccess()) {
                val responseBodyText = response.bodyAsText()
                json.decodeFromString<PromedioEstanques>(responseBodyText)
            } else {
                null
            }
        } catch (e: Exception) {
            // Manejo del error
            null
        }
    }

}
