import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.MaterialTheme
import androidx.compose.material.OutlinedTextField
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.material.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import components.CustomButton
import kotlinx.coroutines.launch
import model.MessageinBubble
import viewModel.ChatViewModel

@Composable
fun ChatScreen(chatViewModel: ChatViewModel) {
    // Obtener los mensajes directamente desde el ViewModel
    val messages = chatViewModel.messages
    var userInput by remember { mutableStateOf("") }
    val scrollState = rememberScrollState()
    val coroutineScope = rememberCoroutineScope()

    // Efecto para hacer scroll automático al recibir nuevos mensajes
    LaunchedEffect(messages.size) {
        coroutineScope.launch {
            scrollState.animateScrollTo(scrollState.maxValue) // Scroll automático al final
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp)
    ) {
        // Historial de chat
        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
        ) {
            for (message in messages) {
                ChatBubble(message)
                Spacer(modifier = Modifier.height(8.dp))
            }
        }

        // Barra de entrada de mensajes
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = userInput,
                onValueChange = { userInput = it },
                placeholder = { Text("Realiza una pregunta...") },
                modifier = Modifier.weight(1f),
                keyboardOptions = KeyboardOptions.Default.copy(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    sendMessage(chatViewModel, userInput) { userInput = "" }
                }),
                colors = TextFieldDefaults.outlinedTextFieldColors(
                    focusedBorderColor = Color.Green,
                    unfocusedLabelColor = Color.Green
                )
            )
            Spacer(modifier = Modifier.width(8.dp))
            CustomButton(
                text = "Enviar",
                onClick = {
                    sendMessage(chatViewModel, userInput) { userInput = "" }
                }
            )
        }
    }
}

// Función para enviar mensajes y manejar el estado
private fun sendMessage(chatViewModel: ChatViewModel, userInput: String, onMessageSent: () -> Unit) {
    if (userInput.isNotEmpty()) {
        // Enviar mensaje al ViewModel para agregarlo a la lista y procesarlo
        chatViewModel.getChatGPTResponse(userInput)
        onMessageSent()  // Limpiar el input del usuario
    }
}

@Composable
fun ChatBubble(message: MessageinBubble) {
    val backgroundColor = if (message.isUser) Color(0xFFDCF8C6) else Color(0xFFEFEFEF)
    val alignment = if (message.isUser) Alignment.CenterEnd else Alignment.CenterStart

    Box(
        contentAlignment = alignment,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp)
    ) {
        Surface(
            color = backgroundColor,
            shape = MaterialTheme.shapes.medium,
            elevation = 1.dp,
            modifier = Modifier.padding(4.dp)
        ) {
            Text(
                text = message.content,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.body2
            )
        }
    }
}
