package data

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
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
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.http.isSuccess
import io.ktor.serialization.kotlinx.json.json
import kotlinx.serialization.json.Json
import model.Estanque
import model.OpenAIService
import model.ResponseHttp
import model.SensorData
import model.Usuario

private const val BASE_URL = "http://192.168.1.98:8080"

private val json = Json { ignoreUnknownKeys = true }
private val client: HttpClient = HttpClient(CIO) {
    install(ContentNegotiation) {
        json(json)
    }
}

class ApiConnection {


    val openAIService = OpenAIService(client)
    suspend fun postUser(requestHttp: Usuario): String? {
        return try {
            val response = client.post("$BASE_URL/api/auth") {
                contentType(ContentType.Application.Json)
                setBody(requestHttp)
            }
            val responseBodyText = response.bodyAsText()
            Log.d("API Response openAI", "Response openAI: $responseBodyText")
            // Intenta deserializar la respuesta
            val responseBody = json.decodeFromString<ResponseHttp>(responseBodyText)
            responseBody.accessToken
        } catch (e: Exception) {
            Log.d("API Error", "No se pudo obtener el token de acceso: ${e.message}")
            null  // Devuelve null en caso de error
        }
    }

    suspend fun validation(responseHttp: ResponseHttp): Usuario? {
        return try {
            val response = client.post("$BASE_URL/api/auth/Log") {
                contentType(ContentType.Application.Json)
                setBody(responseHttp)
            }
            Log.d("ApiConnection", "Usuario: $response")
            // Check the HTTP status code
            if (response.status.isSuccess()) {
                val responseBodyText = response.bodyAsText()

                json.decodeFromString<Usuario>(responseBodyText)
            } else {
                Log.d("API Error", "Error: ${response.status.description}")
                null
            }
        } catch (e: Exception) {
            Log.d("API Error", "Error: ${e.message}")
            null
        }
    }

    suspend fun getUsers(token: String): List<Usuario>? {
        return try {
            val response: HttpResponse = client.get("$BASE_URL/api/user") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    accept(ContentType.Application.Json)
                }
            }
            if (response.status.isSuccess()) {
                val responseBodyText = response.bodyAsText()
                Log.d("API Response", "Response: $responseBodyText")
                json.decodeFromString(responseBodyText)
            } else {
                Log.d("API Error", "Error en la respuesta: ${response.status.description}")
                null
            }
        } catch (e: Exception) {
            Log.d("API Error", "No se pudo obtener los usuarios: ${e.message}")
            null
        }
    }


    suspend fun updateUser(token: String, usuario: Usuario): Boolean {
        return try {
            val response: HttpResponse = client.put("$BASE_URL/api/user/${usuario.idUsuario}") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                    contentType(ContentType.Application.Json)
                }
                setBody(usuario)
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            Log.d("API Error", "No se pudo actualizar el usuario: ${e.message}")
            false
        }
    }

    suspend fun deleteUser(token: String, userId: Long): Boolean {
        return try {
            val response: HttpResponse = client.delete("$BASE_URL/api/user/$userId") {
                headers {
                    append(HttpHeaders.Authorization, "Bearer $token")
                }
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            Log.d("API Error", "No se pudo eliminar el usuario: ${e.message}")
            false
        }
    }

    suspend fun createUser(usuario: Usuario): Boolean {
        return try {
            val response: HttpResponse = client.post("$BASE_URL/api/user") {
                headers {
                    contentType(ContentType.Application.Json)
                }
                setBody(usuario)
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            Log.d("API Error", "No se pudo crear el usuario: ${e.message}")
            false
        }
    }

    suspend fun createEstanque(estanque: Estanque, userId: Long): Boolean {
        return try {
            val response: HttpResponse = client.post("$BASE_URL/api/estanque/usuario/$userId") {
                headers {
                    contentType(ContentType.Application.Json)
                }
                setBody(estanque)
            }
            response.status.isSuccess()
        } catch (e: Exception) {
            Log.d("API Error", "No se pudo crear el estanque: ${e.message}")
            false
        }
    }

    suspend fun getEstanques(userId: Long): List<Estanque>? {
        return try {
            val response: HttpResponse = client.get("$BASE_URL/api/estanque/usuario/$userId") {
                headers {
                    accept(ContentType.Application.Json)
                }
            }
            val responseBodyText = response.bodyAsText()
            Log.d("API Response", "Response: $responseBodyText")
            json.decodeFromString(responseBodyText)
        } catch (e: Exception) {
            Log.d("API Error", "No se pudieron obtener los estanques: ${e.message}")
            null
        }
    }

    suspend fun deleteEstanque(estanqueId: Long): Boolean {
        return try {
            val response: HttpResponse =
                client.delete("$BASE_URL/api/estanque/usuario/$estanqueId") {
                }
            response.status.isSuccess()
        } catch (e: Exception) {
            Log.d("API Error", "No se pudo eliminar el estanque: ${e.message}")
            false
        }
    }

    suspend fun updateEstanque(estanque: Estanque): Boolean {
        return try {
            Log.d("ApiConnection", "Estanque: $estanque")
            val response: HttpResponse =
                client.put("$BASE_URL/api/estanque/usuario/${estanque.idEstanque}") {
                    headers {
                        contentType(ContentType.Application.Json)
                    }
                    setBody(estanque)
                }
            response.status.isSuccess()
        } catch (e: Exception) {
            Log.d("API Error", "No se pudo actualizar el estanque: ${e.message}")
            false
        }
    }

    /*-----------------------------------------------------------------------------------------------------
   * -----------------------------------------------------------------------------------------------------
   * --------------------------------------SENSOR DATA------------------------------------------------
   * -----------------------------------------------------------------------------------------------------
   * -----------------------------------------------------------------------------------------------------*/
    suspend fun getSensorData(): SensorData? {
        return try {
            val response: HttpResponse = client.get("$BASE_URL/api/sensors/random") {
                headers {
                    accept(ContentType.Application.Json)
                }
            }
            if (response.status == HttpStatusCode.OK) {
                val responseBodyText = response.bodyAsText()
                Log.d("API Response", "Response: $responseBodyText")
                Json.decodeFromString<SensorData>(responseBodyText)
            } else {
                Log.e("API Error", "Error: ${response.status}")
                null
            }
        } catch (e: Exception) {
            Log.e("Exception", "Exception: ${e.message}")
            null
        }
    }
    /*-----------------------------------------------------------------------------------------------------
       * -----------------------------------------------------------------------------------------------------
       * --------------------------------------CHAT_GPT------------------------------------------------
       * -----------------------------------------------------------------------------------------------------
       * -----------------------------------------------------------------------------------------------------*/





    suspend fun getChatGPTResponse(prompt: String): String {
        return try {
            openAIService.getResponse(prompt)
        } catch (e: Exception) {
            Log.d("API Error", "Error al obtener la respuesta de ChatGPT: ${e.message}")
            "Error al obtener la respuesta de ChatGPT"
        }
    }

}