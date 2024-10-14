package model

import kotlinx.serialization.Serializable

@Serializable
data class Estanque(
    val idEstanque: Long,
    val nombre: String,
    val status: Status,
    val image_name: String,
    val usuario: Usuario? = null,
    val temperatura: Float = 25.0f,
    val ph: Float = 9.0f,
    val nivelAgua: Float = 67.0f
)