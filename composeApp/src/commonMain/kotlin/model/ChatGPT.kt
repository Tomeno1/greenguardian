package model

import kotlinx.serialization.Serializable

// --- OpenAIRequest ---
// Representa una solicitud al API de OpenAI, incluyendo el modelo a usar y una lista de mensajes.
@Serializable
data class OpenAIRequest(
    val model: String,         // Especifica el modelo de lenguaje de OpenAI que se va a utilizar
    val messages: List<Message> // Lista de mensajes que representan la conversación
)

// --- Message ---
// Representa un mensaje en la conversación, con el rol (usuario o asistente) y el contenido.
@Serializable
data class Message(
    val role: String,          // Rol del emisor del mensaje ("user" o "assistant")
    val content: String        // Contenido del mensaje
)

// --- OpenAIResponse ---
// Modelo para la respuesta de OpenAI, que contiene una lista de opciones (choices).
@Serializable
data class OpenAIResponse(
    val choices: List<Choice> = emptyList() // Lista de opciones generadas por el modelo
)

// --- Choice ---
// Representa una opción o respuesta de OpenAI, incluyendo el mensaje generado.
@Serializable
data class Choice(
    val message: Message       // Mensaje generado por el modelo en esta opción
)

// --- OpenAIError ---
// Modelo que describe los detalles de un error devuelto por OpenAI.
@Serializable
data class OpenAIError(
    val message: String,       // Descripción del error
    val type: String,          // Tipo de error
    val param: String?,        // Parámetro que causó el error (opcional)
    val code: String?          // Código del error (opcional)
)

// --- OpenAIErrorResponse ---
// Modelo para la respuesta de error de OpenAI, que contiene un objeto de error.
@Serializable
data class OpenAIErrorResponse(
    val error: OpenAIError     // Detalles del error recibido
)

// --- MessageinBubble ---
// Representa un mensaje en una burbuja de conversación en la interfaz, con contenido y un indicador si es del usuario.
data class MessageinBubble(
    val content: String,       // Contenido del mensaje
    val isUser: Boolean        // Indica si el mensaje es del usuario (true) o del asistente (false)
)
