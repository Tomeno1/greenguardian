package screen

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
import androidx.compose.material.Button
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import components.CustomButton
import components.EstanqueCard
import components.getImageResourceByName
import components.getImageRsourceSensorByName
import moe.tlaster.precompose.navigation.Navigator
import viewModel.EstanqueViewModel
import viewModel.SensorDataViewModel
import viewModel.UsuarioViewModel

@Composable
fun PondScreen(navigator: Navigator, usuarioViewModel: UsuarioViewModel, estanqueViewModel: EstanqueViewModel) {
    val usuario = usuarioViewModel.usuario
    val estanques = estanqueViewModel.estanques
    val errorMessage = remember { mutableStateOf<String?>(null) }

    LaunchedEffect(usuario) {
        usuario?.let {
            estanqueViewModel.loadEstanques(it.idUsuario,
                onSuccess = { /* Puedes actualizar el estado local o manejar la UI aquí */ },
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
        usuario?.let {

            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estanques.size) { index ->
                    val estanque = estanques[index]
                    EstanqueCard(
                        estanqueName = estanque.nombre,
                        plantImage = getImageResourceByName(estanque.image_name), // Reemplaza con la imagen correcta
                        status = estanque.status,
                        onClick = {
                            estanqueViewModel.selectEstanque(estanque)
                            navigator.navigate("/estanque_detail")
                        },
                        buttons = {}
                    )
                }
            }
        } ?: run {
            Text(text = "No se ha cargado la información del usuario.")
        }
        errorMessage.value?.let {
            Text(text = it, color = Color.Red, style = MaterialTheme.typography.body2)
        }
        Spacer(modifier = Modifier.height(16.dp))
    }
}

val  Red = Color(0xFFFF1100) // Dark Red
val Blue = Color(0xFF0027FF) // Dark Blue
val Green = Color(0xFF008F00) // Dark Green
val Yellow = Color(0xFFFFC107) // Dark Yellow (Olive)
val Cyan = Color(0xFF02B6B6) // Dark Cyan
val Magenta = Color(0xFFBB00BB) // Dark Magenta
@Composable
fun PondDetailScreen(navigator: Navigator, estanqueViewModel: EstanqueViewModel, sensorDataViewModel: SensorDataViewModel) {
    val estanque = estanqueViewModel.selectedEstanque
    val sensorData = sensorDataViewModel.sensorData

    LaunchedEffect(Unit) {
        sensorDataViewModel.fetchSensorData()
    }

    if (estanque != null && sensorData != null) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Datos del Sensor ", style = MaterialTheme.typography.h4)
            Spacer(modifier = Modifier.height(16.dp))
            // Mostrar los datos en una cuadrícula
            LazyVerticalGrid(
                columns = GridCells.Fixed(2),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                item {
                    SensorDataItem("Temperatura", sensorData.temperature, "temperatura", Red, maxValue = 100)
                }
                item {
                    SensorDataItem("pH del Agua", sensorData.ph, "ph", Blue, maxValue = 14)
                }
                item {
                    SensorDataItem("TDS", sensorData.tds, "tds", Green, maxValue = 2000)
                }
                item {
                    SensorDataItem("LDR", sensorData.ldr, "ldr", Yellow, maxValue = 100)
                }
                item {
                    SensorDataItem("Humedad", sensorData.humidity, "humedad", Cyan, maxValue = 100)
                }
                item {
                    SensorDataItem("Flujo", sensorData.flow, "flow", Magenta, maxValue = 100)
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
            CustomButton(
                text = "Abrir Camara de Estanque",
                onClick = {
                    navigator.navigate("/camara")
                }
            )
        }
    } else {
        Text("No se ha seleccionado ningún estanque o los datos del sensor no están disponibles.")
    }
}

@Composable
fun SensorDataItem(label: String, value: Double, imageName: String, color: Color, maxValue: Int) {
    val showTooltip = remember { mutableStateOf(false) }

    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(150.dp) // Fija el tamaño de cada tarjeta
    ) {
        Card(
            modifier = Modifier
                .clickable { showTooltip.value = !showTooltip.value }
                .fillMaxSize(), // Asegura que el contenido llene la tarjeta
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
        modifier = Modifier.size(100.dp) // Tamaño mayor para el círculo de progreso
    ) {
        CircularProgressIndicator(
            progress = 1f,
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer { alpha = 0.3f },
            color = color.copy(alpha = 0.3f) // Progreso restante en color más oscuro
        )
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.size(100.dp),
            color = color // Progreso actual
        )
        Text(
            text = text,
            style = MaterialTheme.typography.body1,
            color = color
        )
    }
}
