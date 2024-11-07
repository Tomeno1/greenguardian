package model

import kotlinx.serialization.Serializable

// --- ResponseHttp ---
// Modelo que representa la respuesta de una solicitud HTTP que contiene un token de autenticación.
@Serializable
data class ResponseHttp(
    val token: String // Token de autenticación recibido en la respuesta HTTP
)
