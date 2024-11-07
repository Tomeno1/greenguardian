package model

import kotlinx.serialization.Serializable

// --- MessageMqtt ---
// Modelo que representa un mensaje enviado o recibido a través del protocolo MQTT.
@Serializable
data class MessageMqtt(
    val message: String // Contenido del mensaje MQTT
)
