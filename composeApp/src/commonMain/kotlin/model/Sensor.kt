package model

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector


data class GuideStepData(
    val stepNumber: Int,
    val title: String,
    val description: String,
    val icon: ImageVector,
    val iconTint: Color
)