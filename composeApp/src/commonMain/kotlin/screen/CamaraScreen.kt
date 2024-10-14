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


@Composable
fun CamaraScreen(navigator: Navigator) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.cultivo), // Reemplaza con tu recurso de imagen
            contentDescription = "Simulación de cámara",
            modifier = Modifier.fillMaxSize()
        )
    }
}
