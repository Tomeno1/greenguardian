package model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

// --- GuideStepData ---
// Modelo que representa un paso en una guía, con información sobre el número de paso, título,
// descripción, ícono visual y color de dicho ícono.
data class GuideStepData(
    val stepNumber: Int,       // Número de paso en la guía
    val title: String,         // Título descriptivo del paso
    val description: String,   // Descripción detallada de lo que implica el paso
    val icon: ImageVector,     // Ícono visual que representa el paso
    val iconTint: Color        // Color del ícono para destacar el paso visualmente
)
