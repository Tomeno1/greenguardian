package model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class DataSensor(
    val label: String,      // Nombre del sensor (ej: "Temperatura")
    val value: Int,         // Valor del sensor (ej: 25 grados)
    val imageName: String,  // Nombre de la imagen asociada (ej: "temperatura")
    val color: Color,       // Color de la representación del sensor (ej: Color.Blue)
    val maxValue: Int       // Valor máximo esperado para el sensor (ej: 100)
)

data class GuideStepData(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconTint: Color
)