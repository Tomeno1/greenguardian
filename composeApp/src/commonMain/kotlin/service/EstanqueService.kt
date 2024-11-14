package service

import android.util.Log
import data.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.headers
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
import io.ktor.http.HttpHeaders
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import model.Estanque

// Servicio para manejar operaciones CRUD en el modelo Estanque
class EstanqueService(private val client: HttpClient) {
    private val baseUrl = "${Config.BASE_URL}/estanques" // URL base para el servicio de estanques

    // Obtener todos los estanques disponibles
    suspend fun getAllEstanques(): List<Estanque>? {
        return try {
            // Realiza una solicitud GET para obtener la lista completa de estanques
            val response: HttpResponse = client.get(baseUrl) {
                accept(ContentType.Application.Json) // Define el tipo de respuesta esperada
            }
            if (response.status.isSuccess()) {
                response.body() // Deserializa y retorna la lista de estanques si la respuesta es exitosa
            } else {
                null // Retorna null si la respuesta no es exitosa
            }
        } catch (e: Exception) {
            null // Retorna null si ocurre una excepción
        }
    }

    // Obtener un estanque específico por ID con autorización
    suspend fun getEstanqueById(token: String, idEstanque: Long): Estanque? {
        return try {
            // Realiza una solicitud GET con token de autorización en los headers
            val response: HttpResponse = client.get("$baseUrl/$idEstanque") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token") // Añade el token para autorización
                    accept(ContentType.Application.Json)
                }
            }
            if (response.status.isSuccess()) {
                val estanque: Estanque = response.body() // Deserializa la respuesta a un objeto Estanque
                Log.d("EstanqueService", "Estanque obtenido: $estanque") // Log de éxito
                estanque
            } else {
                Log.e("EstanqueService", "Error en la respuesta de la API: ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("EstanqueService", "Error al obtener el estanque: ${e.message}", e)
            null
        }
    }

    // Crear un nuevo estanque en el sistema
    suspend fun createEstanque(estanque: Estanque): Estanque? {
        return try {
            // Realiza una solicitud POST para crear un nuevo estanque
            val response: HttpResponse = client.post(baseUrl) {
                contentType(ContentType.Application.Json) // Define el tipo de contenido como JSON
                setBody(estanque) // Envía el objeto Estanque en el cuerpo de la solicitud
            }
            if (response.status == HttpStatusCode.Created) {
                response.body() // Deserializa y retorna el objeto Estanque creado
            } else {
                null // Retorna null si la creación no es exitosa
            }
        } catch (e: Exception) {
            null // Retorna null si ocurre una excepción
        }
    }

    // Actualizar un estanque existente
    suspend fun updateEstanque(token: String, idEstanque: Long, estanque: Estanque): Estanque? {
        return try {
            // Realiza una solicitud PUT para actualizar un estanque específico
            val response: HttpResponse = client.put("$baseUrl/$idEstanque") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token") // Añade el token para autorización
                    contentType(ContentType.Application.Json)
                }
                setBody(estanque) // Envía el objeto Estanque actualizado en el cuerpo de la solicitud
            }
            if (response.status.isSuccess()) {
                response.body() // Deserializa y retorna el objeto Estanque actualizado
            } else {
                null // Retorna null si la actualización no es exitosa
            }
        } catch (e: Exception) {
            null // Retorna null si ocurre una excepción
        }
    }

    // Eliminar un estanque por ID
    suspend fun deleteEstanque(idEstanque: Long): Boolean {
        return try {
            // Realiza una solicitud DELETE para eliminar el estanque especificado
            val response: HttpResponse = client.delete("$baseUrl/$idEstanque") {
                accept(ContentType.Application.Json) // Define el tipo de respuesta esperada
            }
            response.status == HttpStatusCode.NoContent // Retorna true si la eliminación es exitosa
        } catch (e: Exception) {
            false // Retorna false si ocurre una excepción
        }
    }
}
