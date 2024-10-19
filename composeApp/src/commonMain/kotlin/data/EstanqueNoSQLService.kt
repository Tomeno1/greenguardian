package data

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.statement.HttpResponse
import io.ktor.http.isSuccess
import model.EstanqueNoSQL

class EstanqueNoSQLService(private val client: HttpClient) {
    private val baseUrl = "http://192.168.1.98:8080/api/estanque"

    suspend fun getUltimoEstanque(idEstanque: Int): Result<EstanqueNoSQL> {
        return try {
            val response: HttpResponse = client.get("$baseUrl/ultimo/$idEstanque")
            if (response.status.isSuccess()) {
                Result.success(response.body<EstanqueNoSQL>())
            } else {
                Result.failure(Exception("Error al obtener el último estanque"))
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Result.failure(e)  // Retornar un resultado de error en caso de excepción
        }
    }

}