package model

import kotlinx.serialization.Serializable

@Serializable
data class Tarea(
    val id: Long,
    val nombre: String,
    val descripcion: String,
    val activa: Boolean,
    val idUsuario: Long
)
