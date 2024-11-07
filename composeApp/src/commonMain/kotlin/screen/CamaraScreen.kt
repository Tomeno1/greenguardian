package screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import moe.tlaster.precompose.navigation.Navigator
import techminds.greenguardian.R


// --- CamaraScreen ---
// Composable que representa una pantalla de cámara simulada, mostrando una imagen a tamaño completo.
@Composable
fun CamaraScreen(navigator: Navigator) {
    Box(
        modifier = Modifier.fillMaxSize(),             // Ocupa toda la pantalla
        contentAlignment = Alignment.Center            // Centra el contenido dentro del Box
    ) {
        Image(
            painter = painterResource(id = R.drawable.cultivo), // Recurso de imagen simulado
            contentDescription = "Simulación de cámara",
            modifier = Modifier.fillMaxSize()           // La imagen se ajusta a toda la pantalla
        )
    }
}