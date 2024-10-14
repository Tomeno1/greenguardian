package model

import kotlinx.serialization.Serializable

@Serializable
data class Usuario(
    val idUsuario: Long,
    val nombre: String,
    val contraseña: String,
    val role: String,
    var estanques: List<Estanque> = emptyList()

)

