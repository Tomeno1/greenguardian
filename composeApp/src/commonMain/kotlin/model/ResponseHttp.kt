package model

import kotlinx.serialization.Serializable

@Serializable
data class ResponseHttp(
    val token: String
)
