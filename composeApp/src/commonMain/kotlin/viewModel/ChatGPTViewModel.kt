package viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import service.OpenAIService
import kotlinx.coroutines.launch
import model.MessageinBubble
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class ChatViewModel(private val openAIService: OpenAIService) : ViewModel() {

    init {
        Log.d("viewModel.ChatViewModel", "viewModel.ChatViewModel creado")
    }

    // Lista de mensajes para el chat que mantiene la conversación entre el usuario y el bot
    var messages = mutableStateListOf<MessageinBubble>()
        private set

    // Guarda el último tema de conversación relacionado con hidroponía para continuar en caso de preguntas de seguimiento
    private var lastResponseTopic: String? = null

    // Función para verificar si un mensaje contiene palabras relacionadas con hidroponía
    private fun isRelatedToHydroponics(prompt: String): Boolean {
        val keywords = listOf(
            "planta", "verdura", "hidroponía", "cultivo", "agua", "nutrientes", "estanque hidroponico",
            "sistemas de riego", "invernadero", "fotosíntesis", "sustrato", "semilla", "raíces",
            "luz solar", "pH", "fertilizantes", "microorganismos", "oxígeno", "clorofila",
            "nutrientes minerales", "biomasa", "solución nutritiva", "vegetales", "humedad",
            "transpiración", "temperatura", "tubería", "control climático", "bomba de agua",
            "cultivo vertical", "agronomía", "sostenibilidad", "suministro de agua", "cosecha",
            "eficiencia del agua", "residuos orgánicos"
        )
        return keywords.any { keyword -> prompt.contains(keyword, ignoreCase = true) }
    }

    // Función para identificar frases corteses, como "gracias", y responder adecuadamente
    private fun isPolitePhrase(prompt: String): Boolean {
        val politeKeywords = listOf("gracias", "muchas gracias", "thank you")
        return politeKeywords.any { polite -> prompt.contains(polite, ignoreCase = true) }
    }

    // Función para identificar preguntas de seguimiento, usando ciertas palabras clave
    private fun isFollowUpQuestion(prompt: String): Boolean {
        val followUpKeywords = listOf("puedes decirme más", "más información", "explícame", "detalles")
        return followUpKeywords.any { followUp -> prompt.contains(followUp, ignoreCase = true) }
    }

    // Función principal para gestionar la respuesta de ChatGPT en función del mensaje del usuario
    fun getChatGPTResponse(prompt: String) {
        viewModelScope.launch {
            // Añade el mensaje del usuario a la lista de mensajes del chat
            messages.add(MessageinBubble(prompt, isUser = true))

            when {
                // Si el mensaje es una frase de cortesía, responde de forma amigable
                isPolitePhrase(prompt) -> {
                    val botResponse = "¡De nada! Estoy aquí para ayudar en lo que necesites."
                    addBotMessage(botResponse)
                }
                // Si el mensaje es una pregunta de seguimiento y hay un tema previo, pide más información sobre el tema
                isFollowUpQuestion(prompt) && lastResponseTopic != null -> {
                    try {
                        val followUpPrompt = "Proporciona más información sobre $lastResponseTopic."
                        val result = openAIService.getResponse(followUpPrompt)
                        addBotMessage(result)
                    } catch (e: Exception) {
                        Log.d("viewModel.ChatViewModel", "Error al obtener más información sobre $lastResponseTopic: ${e.message}")
                        addBotMessage("Error al obtener más información sobre $lastResponseTopic")
                    }
                }
                // Si el mensaje está relacionado con hidroponía, se envía el mensaje a ChatGPT para obtener una respuesta
                isRelatedToHydroponics(prompt) -> {
                    try {
                        val result = openAIService.getResponse(prompt)
                        addBotMessage(result)
                        lastResponseTopic = prompt  // Actualiza el tema de la conversación
                    } catch (e: Exception) {
                        Log.d("viewModel.ChatViewModel", "Error al obtener la respuesta de ChatGPT: ${e.message}")
                        addBotMessage("Error al obtener la respuesta de ChatGPT")
                    }
                }
                // Si el mensaje no está relacionado con hidroponía, responde indicando que el tema no está soportado
                else -> {
                    addBotMessage("Lo siento, solo puedo responder preguntas relacionadas a la hidroponía.")
                }
            }
        }
    }

    // Función para añadir un mensaje del bot al chat
    private fun addBotMessage(message: String) {
        messages.add(MessageinBubble(message, isUser = false))
    }
}
