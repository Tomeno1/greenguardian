package model

import kotlinx.serialization.Serializable

@Serializable
data class ResponseHttp(
    val accessToken: String
)
