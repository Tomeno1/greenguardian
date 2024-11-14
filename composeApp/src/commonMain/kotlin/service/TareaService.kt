package service

import android.util.Log
import data.Config
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
import kotlinx.serialization.json.Json
import model.Tarea

// Servicio que interactúa con el backend para operaciones CRUD de Tareas
class TareaService(private val client: HttpClient) {
    private val baseUrl = "${Config.BASE_URL}/tareas" // URL base del endpoint de tareas

    // Obtener todas las tareas
    suspend fun getAllTareas(token: String): List<Tarea>? {
        return try {
            val response: HttpResponse = client.get(baseUrl) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token") // Autenticación con token
                    accept(ContentType.Application.Json) // Especifica el tipo de respuesta esperado
                }
            }
            if (response.status.isSuccess()) {
                val responseBodyText = response.bodyAsText()
                Json.decodeFromString<List<Tarea>>(responseBodyText) // Decodifica la respuesta a una lista de tareas
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("TareaService", "Error al obtener todas las tareas: ${e.message}")
            null
        }
    }

    // Obtener una tarea por su ID
    suspend fun getTareaById(token: String, tareaId: Long): Tarea? {
        return try {
            val response: HttpResponse = client.get("$baseUrl/$tareaId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    accept(ContentType.Application.Json)
                }
            }
            if (response.status.isSuccess()) {
                val responseBodyText = response.bodyAsText()
                Json.decodeFromString<Tarea>(responseBodyText) // Decodifica la respuesta a una tarea
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("TareaService", "Error al obtener tarea por ID: ${e.message}")
            null
        }
    }

    // Crear una nueva tarea
    suspend fun createTarea(token: String, tarea: Tarea): Tarea? {
        return try {
            val response: HttpResponse = client.post(baseUrl) {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json) // Define el cuerpo como JSON
                }
                setBody(tarea) // Envia el objeto tarea como cuerpo de la solicitud
            }
            if (response.status.isSuccess()) {
                val responseBodyText = response.bodyAsText()
                Json.decodeFromString<Tarea>(responseBodyText) // Decodifica la respuesta a una tarea
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("TareaService", "Error al crear tarea: ${e.message}")
            null
        }
    }

    // Actualizar una tarea por ID
    suspend fun updateTarea(token: String, tareaId: Long, tarea: Tarea): Tarea? {
        return try {
            val response: HttpResponse = client.put("$baseUrl/$tareaId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                }
                setBody(tarea) // Envia el objeto tarea con los datos actualizados
            }
            if (response.status.isSuccess()) {
                val responseBodyText = response.bodyAsText()
                Json.decodeFromString<Tarea>(responseBodyText) // Decodifica la respuesta a una tarea actualizada
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("TareaService", "Error al actualizar tarea: ${e.message}")
            null
        }
    }

    // Eliminar una tarea por ID
    suspend fun deleteTarea(token: String, tareaId: Long): Boolean {
        return try {
            val response: HttpResponse = client.delete("$baseUrl/$tareaId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            response.status.isSuccess() // Retorna true si la respuesta indica éxito
        } catch (e: Exception) {
            Log.e("TareaService", "Error al eliminar tarea: ${e.message}")
            false
        }
    }

    // Obtener tareas por ID de usuario
    suspend fun getTareasByIdUsuario(token: String, userId: Long): List<Tarea>? {
        return try {
            val response: HttpResponse = client.get("$baseUrl/search-tarea/$userId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    accept(ContentType.Application.Json)
                }
            }
            if (response.status.isSuccess()) {
                val responseBodyText = response.bodyAsText()
                Json.decodeFromString<List<Tarea>>(responseBodyText) // Decodifica la respuesta a una lista de tareas
            } else {
                null
            }
        } catch (e: Exception) {
            Log.e("TareaService", "Error al obtener tareas por ID de usuario: ${e.message}")
            null
        }
    }
}
