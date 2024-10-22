package model

import kotlinx.serialization.Serializable

@Serializable
data class MessageMqtt(
    val message: String
)
