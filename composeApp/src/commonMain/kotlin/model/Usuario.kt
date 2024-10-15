package model

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val idUsuario: Long,
    val nombre: String,
    val apellido: String,
    val email: String,
    val pass: String,
    val role: String,
)
@Serializable
data class AuthUsuario(
    val email: String,
    val pass: String
)