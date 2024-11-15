package viewModel

import service.MqttService
import kotlinx.coroutines.launch
import model.MessageIrrigacion
import model.MessageHorarioRiego
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class MqttViewModel(private val mqttService: MqttService) : ViewModel() {

    // Publica un mensaje genérico en un tema MQTT
    fun publishMessage(
        topic: String,
        message: MessageIrrigacion,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            mqttService.publishMessage(topic, message).fold(
                onSuccess = { onSuccess(it) },
                onFailure = { onError("Error: ${it.message}") }
            )
        }
    }

    // Publica un mensaje de riego en un tema MQTT
    fun publishMessageRiego(
        topic: String,
        message: MessageHorarioRiego,
        onSuccess: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            mqttService.publishMessageRiego(topic, message).fold(
                onSuccess = { onSuccess(it) },
                onFailure = { onError("Error: ${it.message}") }
            )
        }
    }
}
