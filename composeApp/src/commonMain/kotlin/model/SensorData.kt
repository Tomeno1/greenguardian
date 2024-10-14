package model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import kotlinx.serialization.Serializable

@Serializable
data class SensorData(
    val tds: Double,
    val ldr: Double,
    val temperature: Double,
    val humidity: Double,
    val flow: Double,
    val ph: Double
)

data class GuideStepData(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconTint: Color
)