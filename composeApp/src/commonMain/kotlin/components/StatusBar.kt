package components

import androidx.activity.ComponentActivity
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalContext
import androidx.core.view.WindowInsetsControllerCompat

@Composable
fun SetSystemBarsColor(statusBarColor: Color, navigationBarColor: Color, useDarkNavigationIcons: Boolean) {
    val context = LocalContext.current
    val window = (context as? ComponentActivity)?.window ?: return

    // Cambiar el color de la barra de estado superior y los íconos oscuros o claros
    WindowInsetsControllerCompat(window, window.decorView).apply {
        isAppearanceLightStatusBars = false
        window.statusBarColor = statusBarColor.toArgb()
    }
    // Cambiar el color de la barra de navegación inferior y configurar íconos oscuros o claros
    WindowInsetsControllerCompat(window, window.decorView).apply {
        isAppearanceLightNavigationBars = useDarkNavigationIcons
        window.navigationBarColor = navigationBarColor.toArgb()
    }
}
