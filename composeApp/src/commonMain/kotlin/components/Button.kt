package components

import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color

// --- Composable CustomButton ---
// Define un botón personalizado que se puede reutilizar en la interfaz de usuario
@Composable
fun CustomButton(
    text: String,                       // Texto que se mostrará dentro del botón
    onClick: () -> Unit,                 // Acción que se ejecutará al hacer clic en el botón
    backgroundColor: Color = Color(0xFF38D13F),  // Color de fondo predeterminado (verde)
    contentColor: Color = Color.White,   // Color del texto predeterminado (blanco)
    modifier: Modifier = Modifier        // Modificador opcional para personalizar el botón externamente
) {
    Button(
        onClick = onClick,               // Configura la acción que se ejecuta al hacer clic
        colors = ButtonDefaults.buttonColors(
            backgroundColor = backgroundColor,   // Configura el color de fondo del botón
            contentColor = contentColor          // Configura el color del contenido (texto)
        ),
        modifier = modifier              // Aplica cualquier personalización adicional al botón
    ) {
        // Texto del botón que se muestra en pantalla con el color especificado
        Text(text = text, color = contentColor)
    }
}
