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
data class EstanqueNoSQL(
    var sampleTime: Long,
    var idEstanque: Int,
    var deviceData: DeviceData
)

@Serializable
data class DeviceData(
    var humidity: Int,
    var temperature: Int
)

@Serializable
data class EstanqueByUsuarioResponse(
    val nombre: String,
    val listaEstanque: List<Estanque>
)