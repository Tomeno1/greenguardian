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
    val rangoPh: String,
    val idUsuario: Long
)

@Serializable
data class EstanqueNoSQL(
    var sampleTime: Long,
    var idEstanque: Int,
    var deviceData: DeviceData
)

@Serializable
data class PromedioEstanques(
    val humidity: Float,
    val temperature: Float,
    val ph: Float,
    val ec: Float
)

@Serializable
data class DeviceData(
    var humidity: Float,
    var temperature: Float,
    var ph: Float,
    var ec: Float
)

@Serializable
data class EstanqueByUsuarioResponse(
    val nombre: String,
    val listaEstanque: List<Estanque>
)