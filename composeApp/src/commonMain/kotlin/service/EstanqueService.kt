package service

import android.util.Log
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

class EstanqueService(private val client: HttpClient){
    private val baseUrl = "http://192.168.1.98:8080/api/estanques"

    // Obtener todos los estanques
    suspend fun getAllEstanques(): List<Estanque>? {
        return try {
            val response: HttpResponse = client.get(baseUrl) {
                accept(ContentType.Application.Json)
            }
            if (response.status.isSuccess()) {
                response.body()  // Deserializar la respuesta a una lista de estanques
            } else {
            null
            }
        } catch (e: Exception) {
            null
        }
    }

    suspend fun getEstanqueById(token: String, idEstanque: Long): Estanque? {
        return try {
            val response: HttpResponse = client.get("$baseUrl/$idEstanque") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token") // Añadir el token de autorización
                    accept(ContentType.Application.Json)
                }
            }
            if (response.status.isSuccess()) {
                val estanque: Estanque = response.body()  // Deserializar la respuesta a un objeto Estanque
                Log.d("EstanqueService", "Estanque obtenido: $estanque") // Registro exitoso
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


    // Crear un nuevo estanque
    suspend fun createEstanque(estanque: Estanque): Estanque? {
        return try {
            val response: HttpResponse = client.post(baseUrl) {
                contentType(ContentType.Application.Json)
                setBody(estanque)
            }
            if (response.status == HttpStatusCode.Created) {
                response.body()  // Deserializar la respuesta a un objeto Estanque
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Actualizar un estanque
    suspend fun updateEstanque(token: String, idEstanque: Long, estanque: Estanque): Estanque? {
        return try {
            val response: HttpResponse = client.put("$baseUrl/$idEstanque") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                }
                setBody(estanque)
            }
            if (response.status.isSuccess()) {
                response.body()  // Deserializar la respuesta a un objeto Estanque
            } else {
                null
            }
        } catch (e: Exception) {
            null
        }
    }

    // Eliminar un estanque
    suspend fun deleteEstanque(idEstanque: Long): Boolean {
        return try {
            val response: HttpResponse = client.delete("$baseUrl/$idEstanque") {
                accept(ContentType.Application.Json)
            }
            response.status == HttpStatusCode.NoContent  // Devolver true si se eliminó correctamente
        } catch (e: Exception) {
            false
        }
    }

}