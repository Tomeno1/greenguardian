package screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import components.sendLocalNotification
import model.Estado
import moe.tlaster.precompose.navigation.Navigator
import viewModel.EstanqueViewModel
import viewModel.UsuarioViewModel

// --- Pantalla de Estanques ---
@Composable
fun PondScreen(
    navigator: Navigator,
    usuarioViewModel: UsuarioViewModel,
    estanqueViewModel: EstanqueViewModel
) {
    val usuario = usuarioViewModel.usuario
    val estanquesByUsuario = usuarioViewModel.estanquesByUsuario.value
    val errorMessage = remember { mutableStateOf<String?>(null) }

    // Efecto que se ejecuta cuando el usuario cambia
    LaunchedEffect(usuario) {
        usuario?.let {
            // Carga los estanques asociados al usuario
            usuarioViewModel.loadEstanquesByUsuario(it.idUsuario,
                onError = { errorMessage.value = "Error al cargar estanques" }
            )
        }
    }

    // Efecto que se ejecuta cuando los estanques están disponibles
    LaunchedEffect(estanquesByUsuario) {
        estanquesByUsuario?.listaEstanque?.forEach { estanque ->
            estanqueViewModel.loadEstanqueNoSQLById(estanque.idEstanque.toInt())
        }
    }

    // Diseño principal de la pantalla
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        if (estanquesByUsuario != null) {
            // Muestra una cuadrícula adaptable con los estanques
            LazyVerticalGrid(
                columns = GridCells.Adaptive(minSize = 150.dp),
                contentPadding = PaddingValues(8.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(estanquesByUsuario.listaEstanque.size) { index ->
                    val estanque = estanquesByUsuario.listaEstanque[index]
                    val estadoEstanque = estanqueViewModel.determineEstanqueEstado(
                        estanqueViewModel.selectedEstanqueNoSQL.value,
                        estanque
                    )

                    EstanqueCard(
                        estanqueName = "Estanque: ${estanque.idEstanque}",
                        plantImage = getImageResourceByName("lechuga"),
                        estado = estadoEstanque,
                        onClick = {
                            // Registro en la consola al hacer clic
                            Log.d("EstanqueClick", "Estanque ${estanque.idEstanque} seleccionado")
                            // Carga los datos del estanque seleccionado
                            estanqueViewModel.loadEstanqueNoSQLById(estanque.idEstanque.toInt())
                            estanqueViewModel.loadEstanqueById(estanque.idEstanque)
                            // Navega a la pantalla de sensores con el ID del estanque
                            navigator.navigate("/sensorScreen/${estanque.idEstanque}")
                        },
                        buttons = {} // Puedes agregar botones adicionales si lo requieres
                    )
                }
            }
        } else {
            // Mensaje de carga o de error si no hay estanques
            Text(text = "Cargando estanques o no se encontraron estanques.")
        }

        // Mostrar mensaje de error si existe
        errorMessage.value?.let {
            Text(text = it, color = Color.Red, style = MaterialTheme.typography.body2)
        }
    }
}



@Composable
fun SensorDataItem(
    label: String,
    value: Float,
    range: ClosedRange<Float>, // Rango en ClosedRange<Float>
    imageName: String,
    color: Color,
    context: Context
) {
    val showTooltip = remember { mutableStateOf(false) }
    var isNotificationSent by remember { mutableStateOf(false) }

    // Calcula el progreso para el indicador circular, normalizado entre 0 y 1
    val progress = ((value - range.start) / (range.endInclusive - range.start)).coerceIn(0f, 1f)

    // Verificar si el valor del sensor está fuera del rango
    if (value !in range) {
        if (!isNotificationSent) { // Envía notificación solo si no se ha enviado antes
            val alertMessage = "$label está fuera del rango (${range.start} - ${range.endInclusive}). Valor actual: $value"
            Log.d("SensorDataItem", alertMessage)

            // Enviar la notificación
            sendLocalNotification(
                context = context,
                title = "Alerta de $label",
                message = alertMessage,
                notificationId = (label.hashCode() + value.toInt()) // ID único por sensor y valor
            )

            // Marcar que ya se envió la notificación
            isNotificationSent = true
        }
    } else {
        // Si el valor vuelve al rango normal, resetear el estado para futuras notificaciones
        isNotificationSent = false
        Log.d("SensorDataItem", "$label dentro del rango: $value (rango: ${range.start} - ${range.endInclusive})")
    }

    // Diseño del elemento de sensor
    Box(
        modifier = Modifier
            .padding(8.dp)
            .size(150.dp)
    ) {
        Card(
            shape = RoundedCornerShape(8.dp),
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
                    progress = progress,
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
                        CustomButton(text = "Cerrar", onClick = { showTooltip.value = false })
                    }
                }
            }
        }
    }
}



// --- Indicador circular con texto ---
@Composable
fun CircularProgressWithText(progress: Float, text: String, color: Color) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier.size(100.dp)
    ) {
        // Indicador circular de fondo (100%)
        CircularProgressIndicator(
            progress = 1f,
            modifier = Modifier
                .size(100.dp)
                .graphicsLayer { alpha = 0.3f }, // Transparencia para el fondo
            color = color.copy(alpha = 0.3f)
        )
        // Indicador circular del progreso actual
        CircularProgressIndicator(
            progress = progress,
            modifier = Modifier.size(100.dp),
            color = color
        )
        // Texto con el valor actual en el centro
        Text(
            text = text,
            style = MaterialTheme.typography.body1,
            color = color
        )
    }
}



// --- Vista previa para desarrollo ---
@Preview(showBackground = true)
@Composable
fun PreviewSimpleDashboardLayout() {
    // Puedes llamar a tus funciones composables aquí para ver una vista previa
    // Por ejemplo:
    // PondScreen(navigator = ..., usuarioViewModel = ..., estanqueViewModel = ...)
}