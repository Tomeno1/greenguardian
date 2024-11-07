package viewModel

import service.MqttService
import kotlinx.coroutines.launch
import model.MessageMqtt
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class MqttViewModel(private val mqttService: MqttService) : ViewModel() {

    // Función para publicar un mensaje en un tema MQTT
    fun publishMessage(
        topic: String,                 // Tema al que se enviará el mensaje MQTT
        message: MessageMqtt,           // Mensaje a enviar, contiene el contenido del mensaje en un objeto MessageMqtt
        onSuccess: (String) -> Unit,    // Callback a ejecutar en caso de éxito
        onError: (String) -> Unit       // Callback a ejecutar en caso de error
    ) {
        // Ejecutamos la operación en un contexto asincrónico
        viewModelScope.launch {
            // Llamada al servicio MQTT para publicar el mensaje
            val result = mqttService.publishMessage(topic, message)

            // Evaluamos el resultado de la publicación
            result.fold(
                onSuccess = { response ->
                    onSuccess(response)  // Llamamos al callback de éxito pasando la respuesta del servidor
                },
                onFailure = { error ->
                    // Llamamos al callback de error con el mensaje del error
                    onError("Error: ${error.message}")
                }
            )
        }
    }
}
