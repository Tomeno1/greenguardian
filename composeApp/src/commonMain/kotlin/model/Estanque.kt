package model

import kotlinx.serialization.Serializable

@Serializable
data class Estanque(
    val idEstanque: Long,
    val rangoTemp: String,
    val rangoHum: String,
    val rangoEc: String,
    val rangoLuz: String,
    val horarioRiego: String,
    val idUsuario: Long
)

@Serializable
data class EstanqueByUsuarioResponse(
    val nombre: String,
    val listaEstanque: List<Estanque>
)