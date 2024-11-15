package screen

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.foundation.clickable
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
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Switch
import androidx.compose.material.SwitchDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
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
import model.MessageIrrigacion
import viewModel.EstanqueViewModel
import viewModel.MqttViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SensorScreen(
    estanqueViewModel: EstanqueViewModel,
    mqttViewModel: MqttViewModel,
) {
    val sensorData by estanqueViewModel.selectedEstanqueNoSQL
    val estanqueConfigRanges by estanqueViewModel.selectedEstanque
    var isDialogVisible by remember { mutableStateOf(false) }
    val context = LocalContext.current

    if (estanqueConfigRanges != null && sensorData != null) {
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

            // Incluimos el horario de riego en el panel de control de sensores
            SensorControlPanel(
                mqttViewModel = mqttViewModel,
                estanqueViewModel = estanqueViewModel
            )

            if (isDialogVisible) {
                RangoAlertDialog(
                    onDismiss = { isDialogVisible = false },
                    estanqueViewModel = estanqueViewModel,
                    estanqueSelected = estanqueConfigRanges!!
                )
            }

            Text(
                text = "Datos de los Sensores",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                color = Color.Black
            )

            LazyVerticalGrid(columns = GridCells.Fixed(3)) {
                item {
                    SensorDataItem(
                        label = "Temperatura",
                        value = sensorData!!.deviceData.temperature,
                        range = estanqueViewModel.parseRange(estanqueConfigRanges!!.rangoTemp ?: "0-100"),
                        imageName = "temperatura",
                        color = Color(0xFF2196F3),
                        context = context
                    )
                }
                item {
                    SensorDataItem(
                        label = "Humedad",
                        value = sensorData!!.deviceData.humidity,
                        range = estanqueViewModel.parseRange(estanqueConfigRanges!!.rangoHum ?: "0-100"),
                        imageName = "humedad",
                        color = Color(0xFF4CAF50),
                        context = context
                    )
                }
                item {
                    SensorDataItem(
                        label = "TDS",
                        value = sensorData!!.deviceData.ec,
                        range = estanqueViewModel.parseRange(estanqueConfigRanges!!.rangoEc ?: "1.2-2.2"),
                        imageName = "tds",
                        color = Color(0xFFE0CF34),
                        context = context
                    )
                }
                item {
                    SensorDataItem(
                        label = "PH",
                        value = sensorData!!.deviceData.ph,
                        range = estanqueViewModel.parseRange(estanqueConfigRanges!!.rangoPh ?: "0-14"),
                        imageName = "ph",
                        color = Color(0xFFF33628),
                        context = context
                    )
                }
                item {
                    SensorDataItem(
                        label = "LDR",
                        value = sensorData!!.deviceData.ldr,
                        range = estanqueViewModel.parseRange(estanqueConfigRanges!!.rangoLuz ?: "0-100"),
                        imageName = "ldr",
                        color = Color(0xFFFF9800),
                        context = context
                    )
                }
            }

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
    var rangoHorario by remember { mutableStateOf(estanqueSelected.horarioRiego ?: "0-100") }

    // Lista de etiquetas y rangos para mostrarlos en el diálogo de configuración
    val ranges = listOf(
        "Rango de Temperatura (C°)" to rangoTemp,
        "Rango de Humedad (%)" to rangoHum,
        "Rango de EC (mS/cm)" to rangoEc,
        "Rango de Luz (ldr)" to rangoLuz,
        "Rango de Ph" to rangoPh,
        "Rango de Horario" to rangoHorario
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
                                "Rango de Luz (ldr)" -> rangoLuz = it
                                "Rango de Ph" -> rangoPh = it
                                "Rango de Horario" -> rangoHorario = it
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
                    rangoPh = rangoPh,
                    horarioRiego = rangoHorario
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

@RequiresApi(Build.VERSION_CODES.O)
@Composable
fun SensorControlPanel(
    mqttViewModel: MqttViewModel,
    estanqueViewModel: EstanqueViewModel
) {
    var isIrrigationActive by remember { mutableStateOf(false) }

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(sensorControls) { control ->
            SensorControlCard(
                icon = control.icon,
                label = control.label,
                isActive = if (control.label == "Irrigación") isIrrigationActive else control.isActive,
                onToggle = { isActive ->
                    when (control.label) {
                        "Irrigación" -> {
                            val message = MessageIrrigacion(if (isActive) "1" else "0")
                            mqttViewModel.publishMessage(
                                topic = "esp32/1/sub",
                                message = message,
                                onSuccess = {
                                    isIrrigationActive = isActive
                                    Log.d("MQTT", "Irrigación activada: $message")
                                },
                                onError = { Log.e("MQTT", "Error en irrigación: $it") }
                            )
                        }
                        "Horario" -> {
                            estanqueViewModel.selectedEstanque.value?.let { estanque ->
                                estanqueViewModel.publishIrrigationSchedule(estanque)
                            } ?: Log.e("SensorControlPanel", "Estanque no seleccionado")
                        }
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
