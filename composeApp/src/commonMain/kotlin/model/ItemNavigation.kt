package model

import androidx.compose.ui.graphics.vector.ImageVector

data class ItemNavigation(
    val route: String,
    val label: String,
    val defaultIcon: ImageVector,
    val outlinedIcon: ImageVector
)
