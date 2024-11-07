package model

import androidx.compose.ui.graphics.vector.ImageVector

// --- ItemNavigation ---
// Representa un elemento de navegación en la interfaz de usuario.
data class ItemNavigation(
    val route: String,               // Ruta de la pantalla a la que navega este elemento
    val label: String,               // Etiqueta descriptiva para el elemento de navegación
    val defaultIcon: ImageVector,    // Ícono predeterminado para el elemento de navegación
    val outlinedIcon: ImageVector    // Ícono alternativo (generalmente en estilo outline)
)
