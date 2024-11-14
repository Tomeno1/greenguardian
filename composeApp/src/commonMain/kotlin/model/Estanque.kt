package model

import androidx.compose.ui.graphics.Color
import kotlinx.serialization.Serializable

// --- Estanque ---
// Modelo que representa los datos de un estanque en una base de datos relacional.
@Serializable
data class Estanque(
    val idEstanque: Long,           // ID único del estanque
    val rangoTemp: String,           // Rango de temperatura permitida
    val rangoHum: String,            // Rango de humedad permitida
    val rangoEc: String,             // Rango de conductividad eléctrica (EC)
    val rangoLuz: String,            // Rango de luz permitida
    val horarioRiego: String,        // Horario de riego
    val rangoPh: String,             // Rango de pH permitido
    val idUsuario: Long              // ID del usuario propietario
)

// --- EstanqueNoSQL ---
// Modelo que representa los datos de un estanque almacenados en una base de datos NoSQL.
@Serializable
data class EstanqueNoSQL(
    var sampleTime: Long,            // Tiempo de la muestra
    var idEstanque: Int,             // ID del estanque
    var deviceData: DeviceData       // Datos del dispositivo en ese instante
)

// --- PromedioEstanques ---
// Modelo que almacena los valores promedio de sensores de múltiples estanques.
@Serializable
data class PromedioEstanques(
    val humidity: Float,             // Humedad promedio
    val temperature: Float,          // Temperatura promedio
    val ph: Float,                   // pH promedio
    val ec: Float                    // Conductividad eléctrica promedio
)

// --- DeviceData ---
// Modelo que representa los datos de un dispositivo específico dentro del estanque.
@Serializable
data class DeviceData(
    var humidity: Float,             // Humedad medida por el dispositivo
    var temperature: Float,          // Temperatura medida por el dispositivo
    var ph: Float,                   // pH medido por el dispositivo
    var ec: Float,// Conductividad eléctrica medida por el dispositivo
    var ldr: Float          // Luz medida por el dispositivo
)

// --- EstanqueByUsuarioResponse ---
// Modelo de respuesta que devuelve un nombre de usuario y una lista de sus estanques.
@Serializable
data class EstanqueByUsuarioResponse(
    val nombre: String,              // Nombre del usuario
    val listaEstanque: List<Estanque> // Lista de estanques asociados al usuario
)

enum class Estado(val color: Color) {
    BUEN_ESTADO(Color(0xFF1AB320)),
    ADVERTENCIA(Color(0xFFFFC107)),
    MAL_ESTADO(Color(0xFFF44336)),
    CARGANDO_ESTADO(Color.Gray)
}
