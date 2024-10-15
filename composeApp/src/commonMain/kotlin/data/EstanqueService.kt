package data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.accept
import io.ktor.client.request.delete
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.put
import io.ktor.client.request.setBody
import io.ktor.client.statement.HttpResponse
import io.ktor.http.ContentType
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

    // Obtener un estanque por ID
    suspend fun getEstanqueById(idEstanque: Long): Estanque? {
        return try {
            val response: HttpResponse = client.get("$baseUrl/$idEstanque") {
                accept(ContentType.Application.Json)
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
    suspend fun updateEstanque(idEstanque: Long, estanque: Estanque): Estanque? {
        return try {
            val response: HttpResponse = client.put("$baseUrl/$idEstanque") {
                contentType(ContentType.Application.Json)
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