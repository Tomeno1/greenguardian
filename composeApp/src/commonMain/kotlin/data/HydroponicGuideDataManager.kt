// HydroponicGuideDataManager.kt
package data

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.ui.graphics.Color
import model.GuideStepData

object HydroponicGuideDataManager {

    val guideSteps = listOf(
        GuideStepData(
            stepNumber = 1,
            title = "Preparación del Sistema",
            description = "Elige un sistema hidropónico adecuado para el tipo de plantas que deseas cultivar. Los sistemas populares incluyen el de flujo y reflujo, NFT (película de nutrientes) y el de raíz flotante.",
            icon = Icons.Default.Build,
            iconTint = Color(0xFF3F51B5) // Azul
        ),
        GuideStepData(
            stepNumber = 2,
            title = "Selección de Plantas",
            description = "Selecciona plantas que se adapten bien a la hidroponía, como lechugas, espinacas, fresas o hierbas aromáticas. Asegúrate de que las semillas sean de buena calidad.",
            icon = Icons.Default.Eco,
            iconTint = Color(0xFF4CAF50) // Verde
        ),
        GuideStepData(
            stepNumber = 3,
            title = "Preparar la Solución Nutritiva",
            description = "Mezcla la solución nutritiva siguiendo las instrucciones del fabricante, ajustando el pH al nivel adecuado (generalmente entre 5.5 y 6.5). La concentración de nutrientes también debe ser controlada.",
            icon = Icons.Default.WaterDrop,
            iconTint = Color(0xFF2196F3) // Azul
        ),
        GuideStepData(
            stepNumber = 4,
            title = "Monitoreo y Mantenimiento",
            description = "Verifica regularmente el pH, la conductividad eléctrica (EC) y la temperatura del agua. Asegúrate de limpiar el sistema y cambiar la solución nutritiva periódicamente.",
            icon = Icons.Default.CheckCircle,
            iconTint = Color(0xFFFFA000) // Naranja
        )
    )
}
