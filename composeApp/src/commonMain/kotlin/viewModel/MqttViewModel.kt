package viewModel

import service.MqttService
import kotlinx.coroutines.launch
import model.MessageMqtt
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class MqttViewModel(private val mqttService: MqttService) : ViewModel() {

    fun publishMessage(topic: String, message: MessageMqtt, onSuccess: (String) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val result = mqttService.publishMessage(topic, message)
            result.fold(
                onSuccess = { response ->
                    onSuccess(response)  // Llamamos a la función de éxito con la respuesta
                },
                onFailure = { error ->
                    onError("Error: ${error.message}")  // Llamamos a la función de error con el mensaje del error
                }
            )
        }
    }
}
