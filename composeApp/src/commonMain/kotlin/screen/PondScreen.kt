package screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import components.CustomButton
import components.EstanqueCard
import components.getImageResourceByName
import components.getImageRsourceSensorByName
import model.Status
import moe.tlaster.precompose.navigation.Navigator
import viewModel.TokenViewModel
import viewModel.UsuarioViewModel

@Composable
fun PondScreen(navigator: Navigator, usuarioViewModel: UsuarioViewModel) {
    val usuario = usuarioViewModel.usuario
    val estanquesByUsuario = usuarioViewModel.estanquesByUsuario.value
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(usuario) {
        usuario?.let {
            Log.d("PONDS PANTALLA", "PRUEBA QUE LLEGO ACA")
            usuarioViewModel.loadEstanquesByUsuario(it.idUsuario,
                onError = { errorMessage.value = "Error al cargar estanques" }
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (estanquesByUsuario != null) {
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estanquesByUsuario.listaEstanque.size) { index ->
                    val estanque = estanquesByUsuario.listaEstanque[index]
                    EstanqueCard(
                        estanqueName = estanque.idEstanque.toString(),
                        plantImage = getImageResourceByName("lechuga"), // Reemplaza con la imagen correcta
                        status = Status.GOOD, // Actualiza el estado según tus datos
                        onClick = {
                            {}
                        },
                        buttons = {}
                    )
                }
            }
        } else {
            Text(text = "Cargando estanques o no se encontraron estanques.")
        }

        errorMessage.value?.let {
            Text(text = it, color = Color.Red, style = MaterialTheme.typography.body2)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

@Composable
fun SensorDataItem(label: String, value: Double, imageName: String, color: Color, maxValue: Int) {
    val showTooltip = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(150.dp)
    ) {
        Card(
            modifier = Modifier
                .clickable { showTooltip.value = !showTooltip.value }
                .fillMaxSize(),
            elevation = 4.dp
        ) {
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier.fillMaxSize()
            ) {
                CircularProgressWithText(
                    progress = value.toFloat() / maxValue.toFloat(),
                    text = "${value.toInt()}",
                    color = color
                )
                Image(
                    painter = painterResource(id = getImageRsourceSensorByName(imageName)),
                    contentDescription = label,
                    modifier = Modifier
                        .size(32.dp)
                        .align(Alignment.TopEnd)
                        .padding(4.dp)
                )
            }
        }

        if (showTooltip.value) {
            Popup(
                alignment = Alignment.Center,
                properties = PopupProperties(focusable = false)
            ) {
                Card(
                    backgroundColor = Color.White,
                    elevation = 10.dp,
                    modifier = Modifier
                        .padding(16.dp)
                        .size(200.dp, 120.dp),
                    shape = MaterialTheme.shapes.medium
                ) {
                    Column(
                        modifier = Modifier
                            .padding(10.dp)
                            .fillMaxSize(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.h6,
                            color = color,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        Text(
                            text = "$value",
                            style = MaterialTheme.typography.body1,
                            color = Color.Gray,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                        CustomButton(text = "Cerrar", onClick = { showTooltip.value = false})
                    }
                }
            }
        }
    }
}

@Composable
fun CircularProgressWithText(progress: Float, text: String, color: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(100.dp)
    ) {
        CircularProgressIndicator(
            progress = 1f,
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer { alpha = 0.3f },
            color = color.copy(alpha = 0.3f)
        )
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.size(100.dp),
            color = color
        )
        Text(
            text = text,
            style = MaterialTheme.typography.body1,
            color = color
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PreviewSimpleDashboardLayout() {
    SensorDataItem(label = "Temperatura", value = 25.0, imageName = "temperatura", color = Color.Blue, maxValue = 100)

}