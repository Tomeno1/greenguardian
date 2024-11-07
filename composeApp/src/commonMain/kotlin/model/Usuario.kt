package model

import kotlinx.serialization.Serializable

// --- Usuario ---
// Modelo que representa un usuario en el sistema, con información de identificación y credenciales.
@Serializable
data class Usuario(
    val idUsuario: Long,    // ID único del usuario
    val nombre: String,     // Nombre del usuario
    val apellido: String,   // Apellido del usuario
    val email: String,      // Dirección de correo electrónico del usuario
    val pass: String,       // Contraseña del usuario
    val role: String        // Rol del usuario (por ejemplo, administrador, usuario, etc.)
)

// --- AuthUsuario ---
// Modelo que representa las credenciales necesarias para la autenticación de un usuario.
@Serializable
data class AuthUsuario(
    val email: String,      // Correo electrónico del usuario para autenticación
    val pass: String        // Contraseña del usuario para autenticación
)
