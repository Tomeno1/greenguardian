package service

import data.Config
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import model.EstanqueNoSQL

// Servicio para interactuar con datos del estanque en una base de datos NoSQL
class EstanqueNoSQLService(private val client: HttpClient) {
    private val baseUrl = "${Config.BASE_URL}/estanque" // URL base del endpoint para estanques

    // Función para obtener el último estado del estanque según el idEstanque dado
    suspend fun getUltimoEstanque(idEstanque: Int): Result<EstanqueNoSQL> {
        return try {
            // Realiza una solicitud GET a la URL específica para obtener el último estado del estanque
            val response: HttpResponse = client.get("$baseUrl/ultimo/$idEstanque")
            if (response.status.isSuccess()) { // Verifica si la respuesta es exitosa
                Result.success(response.body<EstanqueNoSQL>()) // Decodifica y retorna el cuerpo de la respuesta como EstanqueNoSQL
            } else {
                Result.failure(Exception("Error al obtener el último estanque")) // Retorna un error si la respuesta no es exitosa
            }
        } catch (e: Exception) {
            e.printStackTrace() // Imprime la excepción en la consola para depuración
            Result.failure(e)  // Retorna un resultado de error en caso de excepción
        }
    }
}
