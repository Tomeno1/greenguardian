package components

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun SetSystemBarsColor(
    statusBarColor: Color,             // Color para la barra de estado (superior)
    navigationBarColor: Color,          // Color para la barra de navegación (inferior)
    useDarkNavigationIcons: Boolean     // Define si los íconos deben ser oscuros o claros
) {
    val context = LocalContext.current
    val window = (context as? ComponentActivity)?.window ?: return

    // --- Configuración de la Barra de Estado (Status Bar) ---
    WindowInsetsControllerCompat(window, window.decorView).apply {
        isAppearanceLightStatusBars =
            false                  // No activa íconos oscuros en la barra de estado
        window.statusBarColor =
            statusBarColor.toArgb()      // Aplica el color especificado a la barra de estado
    }

    // --- Configuración de la Barra de Navegación (Navigation Bar) ---
    WindowInsetsControllerCompat(window, window.decorView).apply {
        isAppearanceLightNavigationBars =
            useDarkNavigationIcons  // Configura los íconos oscuros/claro según el parámetro
        window.navigationBarColor =
            navigationBarColor.toArgb()   // Aplica el color especificado a la barra de navegación
    }
}
