package screen

import android.content.Context
import android.util.Log
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Schedule
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.*
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
) {
    val context = LocalContext.current
    val sensorData by estanqueViewModel.selectedEstanqueNoSQL
    val estanqueConfigRanges by estanqueViewModel.selectedEstanque
    var isDialogVisible by remember { mutableStateOf(false) }

    if (estanqueConfigRanges != null && sensorData != null) {
        val configEstanqueRanges = estanqueConfigRanges
        val dataSensor = sensorData
        if (configEstanqueRanges != null && dataSensor != null) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(8.dp)
            ) {
                Text(
                    text = "Panel de Control",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    color = Color.Black
                )

                // Panel de control de sensores con botones de activación/desactivación
                SensorControlPanel(mqttViewModel = mqttViewModel)

                // Diálogo para configurar rangos de sensores, activado por `isDialogVisible`
                if (isDialogVisible) {
                    RangoAlertDialog(
                        onDismiss = { isDialogVisible = false },
                        estanqueViewModel = estanqueViewModel,
                        estanqueSelected = configEstanqueRanges
                    )
                }

                // Sección de datos de los sensores
                Text(
                    text = "Datos de los Sensores",
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.h6,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(8.dp),
                    color = Color.Black
                )

                // Cuadrícula de datos de sensores, mostrando temperatura, humedad, TDS y pH
                LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                    item {
                        SensorDataItem(
                            context = context,
                            sensorId = 1,
                            label = "Temperatura",
                            value = dataSensor.deviceData.temperature,
                            range = parseRange(configEstanqueRanges.rangoTemp ?: "0-100"),
                            imageName = "temperatura",
                            color = Color(0xFF2196F3),

                            )
                    }
                    item {
                        SensorDataItem(
                            context = context,
                            sensorId = 2,
                            label = "Humedad",
                            value = dataSensor.deviceData.humidity,
                            range = parseRange(configEstanqueRanges.rangoHum ?: "0-100"),
                            imageName = "humedad",
                            color = Color(0xFF4CAF50),

                            )
                    }
                    item {
                        SensorDataItem(
                            context = context,
                            sensorId = 3,
                            label = "TDS",
                            value = dataSensor.deviceData.ec,
                            range = parseRange(configEstanqueRanges.rangoEc ?: "1.2-2.2"),
                            imageName = "tds",
                            color = Color(0xFFE0CF34),

                            )
                    }
                    item {
                        SensorDataItem(
                            context = context,
                            sensorId = 4,
                            label = "PH",
                            value = dataSensor.deviceData.ph,
                            range = parseRange(configEstanqueRanges.rangoPh ?: "0-100"),
                            imageName = "ph",
                            color = Color(0xFFF33628),

                            )
                    }
                }
                // Botón para abrir el diálogo de configuración de rangos de sensores
                Button(
                    onClick = { isDialogVisible = true },
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF38D13F))
                ) {
                    Text("Configurar Rangos de Sensores", color = Color.White)
                }
            }
        }
    } else {
        Text(text = "Cargando datos de sensores...")
    }

}


@Composable
fun RangoAlertDialog(
    onDismiss: () -> Unit,
    estanqueViewModel: EstanqueViewModel,
    estanqueSelected: Estanque
) {
    // Estados para los rangos de configuración de cada sensor
    var rangoTemp by remember { mutableStateOf(estanqueSelected.rangoTemp ?: "0-100") }
    var rangoHum by remember { mutableStateOf(estanqueSelected.rangoHum ?: "0-100") }
    var rangoEc by remember { mutableStateOf(estanqueSelected.rangoEc ?: "0-100") }
    var rangoLuz by remember { mutableStateOf(estanqueSelected.rangoLuz ?: "0-100") }
    var rangoPh by remember { mutableStateOf(estanqueSelected.rangoPh ?: "0-100") }

    // Lista de etiquetas y rangos para mostrarlos en el diálogo de configuración
    val ranges = listOf(
        "Rango de Temperatura (C°)" to rangoTemp,
        "Rango de Humedad (%)" to rangoHum,
        "Rango de EC (mS/cm)" to rangoEc,
        "Rango de Luz (lux)" to rangoLuz,
        "Rango de Ph" to rangoPh
    )

    AlertDialog(
        onDismissRequest = { onDismiss() },
        title = { Text("Configurar Rangos de Sensores") },
        text = {
            Column {
                ranges.forEach { (label, rangeValue) ->
                    SensorRangeInput(
                        label = label,
                        rangeValue = rangeValue,
                        onValueChange = {
                            when (label) {
                                "Rango de Temperatura (C°)" -> rangoTemp = it
                                "Rango de Humedad (%)" -> rangoHum = it
                                "Rango de EC (mS/cm)" -> rangoEc = it
                                "Rango de Luz (lux)" -> rangoLuz = it
                                "Rango de Ph" -> rangoPh = it
                            }
                        }
                    )
                }
            }
        },
        confirmButton = {
            Button(onClick = {
                // Crear y actualizar el estanque con los nuevos valores
                val updatedEstanque = estanqueSelected.copy(
                    rangoTemp = rangoTemp,
                    rangoHum = rangoHum,
                    rangoEc = rangoEc,
                    rangoLuz = rangoLuz,
                    rangoPh = rangoPh
                )
                // Llamada al ViewModel para actualizar el estanque
                estanqueViewModel.updateEstanque(
                    idEstanque = updatedEstanque.idEstanque,
                    estanque = updatedEstanque,
                    onSuccess = {
                        Log.d("RangoAlertDialog", "Actualización exitosa")
                        estanqueViewModel.loadEstanqueById(updatedEstanque.idEstanque) // Recargar después de actualizar
                        onDismiss()
                    },
                    onError = { error ->
                        Log.e("RangoAlertDialog", "Error al actualizar el estanque: $error")
                    }
                )
            }, colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF38D13F))) {
                Text(color = Color.White, text = "Guardar")
            }
        },
        dismissButton = {
            Button(
                onClick = onDismiss,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color.Red)
            )
            {
                Text(color = Color.White, text = "Cancelar")
            }
        }
    )
}

@Composable
fun SensorRangeInput(label: String, rangeValue: String, onValueChange: (String) -> Unit) {
    // Campo de entrada para modificar el rango del sensor en el diálogo
    OutlinedTextField(
        value = rangeValue,
        onValueChange = onValueChange,
        label = { Text(label) },
        colors = TextFieldDefaults.outlinedTextFieldColors(
            focusedBorderColor = Color(0xFF38C93E),
            unfocusedBorderColor = Color(0xFFB0BEC5),
            cursorColor = Color(0xFF38C93E),
            focusedLabelColor = Color(0xFF38C93E),
            unfocusedLabelColor = Color(0xFF757575)
        )
    )
}
// Función para convertir el rango en una tupla de flotantes
fun parseRange(range: String): Pair<Float, Float> {
    val rangeParts = range.split("-")
    return if (rangeParts.size == 2) {
        Pair(rangeParts[0].toFloatOrNull() ?: 0f, rangeParts[1].toFloatOrNull() ?: 100f)
    } else {
        Pair(0f, 100f)
    }
}

@Composable
fun SensorControlPanel(mqttViewModel: MqttViewModel) {
    // Estado para manejar si el riego (Irrigación) está activo o no
    var isIrrigationActive by remember { mutableStateOf(false) }

    // Componente de fila de desplazamiento horizontal que contiene los controles de sensores
    LazyRow(
        modifier = Modifier
            .fillMaxWidth(),  // Ocupa todo el ancho de la pantalla
        horizontalArrangement = Arrangement.spacedBy(8.dp)  // Espaciado entre los elementos
    ) {
        // Itera sobre cada elemento en `sensorControls`, una lista de datos para los controles de sensores
        items(sensorControls) { sensorControl ->
            SensorControlCard(
                icon = sensorControl.icon,  // Icono que representa el control
                label = sensorControl.label,  // Etiqueta que muestra el nombre del control
                isActive = if (sensorControl.label == "Irrigación") isIrrigationActive else sensorControl.isActive,
                onToggle = { isActive ->
                    // Si el control es "Irrigación", envía un mensaje MQTT para activar o desactivar el riego
                    if (sensorControl.label == "Irrigación") {
                        val message = if (isActive) "1" else "0"  // Activa con "1" y desactiva con "0"

                        // Publica el mensaje MQTT al tema correspondiente
                        mqttViewModel.publishMessage(
                            topic = "esp32/1/sub",  // Tema MQTT donde se envía el mensaje
                            message = MessageMqtt(message = message),  // Mensaje que contiene "1" o "0"
                            onSuccess = {
                                // Actualiza el estado de `isIrrigationActive` según el valor `isActive`
                                isIrrigationActive = isActive
                            },
                            onError = { error ->
                                // Registra el error en caso de fallo al publicar el mensaje MQTT
                                Log.e("MQTT", "Error: $error")
                            }
                        )
                    }
                }
            )
        }
    }
}


val sensorControls = listOf(
    SensorControlData(Icons.Default.WaterDrop, "Irrigación", true),
    SensorControlData(Icons.Default.Notifications, "Notificaciones", false),
    SensorControlData(Icons.Default.Schedule, "Horario", false)
)

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
    onToggle: (Boolean) -> Unit
) {
    // Tarjeta de control para activar/desactivar funciones como "Irrigación" y "Notificaciones"
    Card(
        shape = RoundedCornerShape(16.dp),
        elevation = 4.dp,
        modifier = Modifier
            .padding(8.dp)
            .width(120.dp)
            .clickable { onToggle(!isActive) } // Hacer el Card presionable
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier.padding(8.dp)
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
