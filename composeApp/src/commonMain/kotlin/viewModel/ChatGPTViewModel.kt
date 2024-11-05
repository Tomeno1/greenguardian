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

    // Lista de mensajes para el chat
    var messages = mutableStateListOf<MessageinBubble>()
        private set

    // Último tema tratado
    private var lastResponseTopic: String? = null

    // Lista de palabras clave relacionadas con hidroponía
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

    // Verificación específica para "gracias"
    private fun isPolitePhrase(prompt: String): Boolean {
        val politeKeywords = listOf("gracias", "muchas gracias", "thank you")
        return politeKeywords.any { polite -> prompt.contains(polite, ignoreCase = true) }
    }

    // Verificación para preguntas de seguimiento
    private fun isFollowUpQuestion(prompt: String): Boolean {
        val followUpKeywords = listOf("puedes decirme más", "más información", "explícame", "detalles")
        return followUpKeywords.any { followUp -> prompt.contains(followUp, ignoreCase = true) }
    }

    // Función para enviar mensajes al ChatGPT
    fun getChatGPTResponse(prompt: String) {
        viewModelScope.launch {
            // Agregar el mensaje del usuario a la lista
            messages.add(MessageinBubble(prompt, isUser = true))

            when {
                isPolitePhrase(prompt) -> {
                    // Respuesta amigable para "gracias"
                    val botResponse = "¡De nada! Estoy aquí para ayudar en lo que necesites."
                    addBotMessage(botResponse)
                }
                isFollowUpQuestion(prompt) && lastResponseTopic != null -> {
                    // Pregunta de seguimiento sobre un tema anterior
                    try {
                        val followUpPrompt = "Proporciona más información sobre $lastResponseTopic."
                        val result = openAIService.getResponse(followUpPrompt)
                        addBotMessage(result)
                    } catch (e: Exception) {
                        Log.d("viewModel.ChatViewModel", "Error al obtener más información sobre $lastResponseTopic: ${e.message}")
                        addBotMessage("Error al obtener más información sobre $lastResponseTopic")
                    }
                }
                isRelatedToHydroponics(prompt) -> {
                    // Pregunta relacionada con hidroponía
                    try {
                        val result = openAIService.getResponse(prompt)
                        addBotMessage(result)
                        lastResponseTopic = prompt  // Guardar el tema tratado
                    } catch (e: Exception) {
                        Log.d("viewModel.ChatViewModel", "Error al obtener la respuesta de ChatGPT: ${e.message}")
                        addBotMessage("Error al obtener la respuesta de ChatGPT")
                    }
                }
                else -> {
                    // Pregunta no relacionada con hidroponía
                    addBotMessage("Lo siento, solo puedo responder preguntas relacionadas a la hidroponía.")
                }
            }
        }
    }

    // Función para agregar el mensaje del bot a la lista
    private fun addBotMessage(message: String) {
        messages.add(MessageinBubble(message, isUser = false))
    }
}
