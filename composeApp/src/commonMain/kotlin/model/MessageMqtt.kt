package model

import kotlinx.serialization.Serializable

// --- MessageMqtt ---
// Modelo que representa un mensaje enviado o recibido a través del protocolo MQTT.
@Serializable
data class MessageIrrigacion(
    val message: String // Contenido del mensaje MQTT
)

@Serializable
data class MessageHorarioRiego(
    val message: String,
    val hora_actual: String,
    val rango_horario: String
)