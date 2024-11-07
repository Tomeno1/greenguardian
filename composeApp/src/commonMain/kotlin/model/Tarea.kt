package model

import kotlinx.serialization.Serializable

// --- Tarea ---
// Modelo que representa una tarea, con información sobre su identificación, nombre, descripción,
// estado (activa o no) y el ID del usuario al que pertenece.
@Serializable
data class Tarea(
    val id: Long,             // ID único de la tarea
    val nombre: String,       // Nombre o título de la tarea
    val descripcion: String,  // Descripción detallada de la tarea
    val activa: Boolean,      // Indica si la tarea está activa o no
    val idUsuario: Long       // ID del usuario propietario de la tarea
)
