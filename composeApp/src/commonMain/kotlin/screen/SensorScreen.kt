package screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.Estanque
import model.MessageMqtt
import viewModel.EstanqueViewModel
import viewModel.MqttViewModel
import viewModel.UsuarioViewModel

@Composable
fun SensorScreen(
    estanqueViewModel: EstanqueViewModel,
    mqttViewModel: MqttViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    // Obtenemos el estado de los datos de sensores desde el ViewModel
    val context = LocalContext.current
    val estanqueNoSQL by estanqueViewModel.selectedEstanqueNoSQL
    var showDialog by remember { mutableStateOf(false) } // Estado para controlar el diálogo de configuración
    val estanqueSelected = usuarioViewModel.estanquesByUsuario.value?.listaEstanque?.find { it.idEstanque.toInt() == estanqueNoSQL?.idEstanque }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Texto "Panel de Control" alineado a la izquierda
        Text(
            text = "Panel de Control",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            color = Color.Black
        )

        // Aquí va el SensorControlPanel en la parte superior
        SensorControlPanel(mqttViewModel = mqttViewModel)

        // Mostrar el diálogo si showDialog está en true
        if (showDialog) {
            RangoAlertDialog(
                onDismiss = { showDialog = false },
                estanque = estanqueSelected,
                estanqueViewModel = estanqueViewModel
            )
        }

        // Mostrar la cuadrícula de datos de sensores
        Text(
            text = "Datos del estanque seleccionado",
            style = MaterialTheme.typography.h6,
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            color = Color.Black
        )
        if (estanqueNoSQL != null) {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                item {
                    SensorDataItem(
                        context =  context,
                        sensorId = 1,
                        label = "Temperatura",
                        value = estanqueNoSQL!!.deviceData.temperature,
                        range = parseRange(
                            estanqueSelected?.rangoTemp ?: "0-100"
                        ), // Rango de temperatura
                        imageName = "temperatura",
                        color = Color(0xFF2196F3),
                        maxValue = 100,
                        onAlert = { message -> Log.d("SensorDataItem", message) } // Enviar alerta si está fuera de rango
                    )
                }
                item {
                    SensorDataItem(
                        context =  context,
                        sensorId = 2,
                        label = "Humedad",
                        value = estanqueNoSQL!!.deviceData.humidity,
                        range = parseRange(
                            estanqueSelected?.rangoHum ?: "0-100"
                        ), // Rango de humedad
                        imageName = "humedad",
                        color = Color(0xFF4CAF50),
                        maxValue = 100,
                        onAlert = { message -> Log.d("SensorDataItem", message) }
                    )
                }
                item {
                    SensorDataItem(
                        context =  context,
                        sensorId = 3,
                        label = "TDS",
                        value = estanqueNoSQL!!.deviceData.ec,
                        range = parseRange(estanqueSelected?.rangoEc ?: "1.2-2.2"), // Rango de EC
                        imageName = "tds",
                        color = Color(0xFFE0CF34),
                        maxValue = 3,
                        onAlert = { message -> Log.d("SensorDataItem", message) }
                    )
                }
                item {
                    SensorDataItem(
                        context =  context,
                        sensorId = 4,
                        label = "PH",
                        value = estanqueNoSQL!!.deviceData.ph,
                        range = parseRange(estanqueSelected?.rangoph ?: "0-100"), // Rango de pH
                        imageName = "ph",
                        color = Color(0xFFF33628),
                        maxValue = 100,
                        onAlert = { message -> Log.d("SensorDataItem", message) }
                    )
                }
            }
        } else {
            Text(text = "Cargando datos de sensores...")
        }
        // Botón para abrir el diálogo de configuración de rangos
        Button(
            onClick = { showDialog = true },
            modifier = Modifier
                .padding(8.dp)
                .fillMaxWidth()
        ) {
            Text("Configurar Rangos de Sensores")
        }


    }
}

// Función para convertir el rango de sensor de String a Pair<Float, Float>
fun parseRange(range: String): Pair<Float, Float> {
    val rangeParts = range.split("-")
    return if (rangeParts.size == 2) {
        Pair(rangeParts[0].toFloatOrNull() ?: 0f, rangeParts[1].toFloatOrNull() ?: 100f)
    } else {
        Pair(0f, 100f) // Rango por defecto
    }
}

@Composable
fun RangoAlertDialog(
    onDismiss: () -> Unit,
    estanque: Estanque?,
    estanqueViewModel: EstanqueViewModel
) {
    var rangoTemp by remember { mutableStateOf(estanque?.rangoTemp ?: "") }
    var rangoHum by remember { mutableStateOf(estanque?.rangoHum ?: "") }
    var rangoEc by remember { mutableStateOf(estanque?.rangoEc ?: "") }
    var rangoLuz by remember { mutableStateOf(estanque?.rangoLuz ?: "") }

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Configurar Rangos de Sensores") },
        text = {
            Column {
                TextField(
                    value = rangoTemp,
                    onValueChange = { rangoTemp = it },
                    label = { Text("Rango de Temperatura (C°)") }
                )
                TextField(
                    value = rangoHum,
                    onValueChange = { rangoHum = it },
                    label = { Text("Rango de Humedad (%)") }
                )
                TextField(
                    value = rangoEc,
                    onValueChange = { rangoEc = it },
                    label = { Text("Rango de EC (mS/cm)") }
                )
                TextField(
                    value = rangoLuz,
                    onValueChange = { rangoLuz = it },
                    label = { Text("Rango de Luz (luz)") }
                )

            }
        },
        confirmButton = {
            Button(onClick = {
                estanque?.let {
                    val updatedEstanque = it.copy(
                        rangoTemp = rangoTemp,
                        rangoHum = rangoHum,
                        rangoEc = rangoEc,
                        rangoLuz = rangoLuz
                    )
                    estanqueViewModel.updateEstanque(it.idEstanque, updatedEstanque, onSuccess = {
                        onDismiss() // Cerrar el diálogo
                    }, onError = {
                        Log.d("Error", "Error al actualizar el estanque")
                    })
                }
            }) {
                Text("Guardar")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancelar")
            }
        }
    )
}


@Composable
fun SensorControlPanel(mqttViewModel: MqttViewModel) {
    // Manejo de estado local para la Irrigación
    var isIrrigationActive by remember { mutableStateOf(false) } // Por defecto, apagado

    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sensorControls) { sensorControl ->
            SensorControlCard(
                icon = sensorControl.icon,
                label = sensorControl.label,
                isActive = if (sensorControl.label == "Irrigación") isIrrigationActive else sensorControl.isActive,
                onToggle = { isActive ->
                    if (sensorControl.label == "Irrigación") {
                        // Cambiar el estado local basado en el switch activado o desactivado
                        val message = if (isActive) "1" else "0" // "1" es encendido, "0" es apagado

                        // Publicar el mensaje a través de MQTT
                        mqttViewModel.publishMessage(
                            topic = "esp32/1/sub",
                            message = MessageMqtt(message = message),
                            onSuccess = {
                                Log.d("MQTT", "Mensaje de irrigación enviado con éxito: $message")
                                isIrrigationActive =
                                    isActive // Actualizamos el estado local después de éxito
                            },
                            onError = { error ->
                                Log.e("MQTT", "Error: $error")
                            }
                        )
                    }
                }
            )
        }
    }
}

// Lista de ejemplos para los controles
val sensorControls = listOf(
    SensorControlData(Icons.Default.WaterDrop, "Irrigación", true),
    SensorControlData(Icons.Default.Notifications, "Notificaciones", false),
    SensorControlData(Icons.Default.Schedule, "Horario", false)
)

// Modelo de datos para los controles
data class SensorControlData(
    val icon: ImageVector,
    val label: String,
    val isActive: Boolean
)

@Composable
fun SensorControlCard(
    icon: ImageVector,
    label: String,
    isActive: Boolean,
    onToggle: (Boolean) -> Unit // Función que se llamará cuando se cambie el estado del interruptor
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        modifier = Modifier
            .padding(8.dp)
            .width(120.dp)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (isActive) Color(0xFF38D13F) else Color.Gray,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = label,
                fontSize = 14.sp,
                color = Color.Gray
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Switch que llama a la función onToggle cuando se cambia el estado
            Switch(
                checked = isActive,
                onCheckedChange = { onToggle(it) },
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color(0xFF38D13F),
                    uncheckedThumbColor = Color.Gray
                )
            )
        }
    }
}
