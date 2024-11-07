package techminds.greenguardian

import App
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent

// Define la clase MainActivity, que extiende ComponentActivity, para utilizar funcionalidades de Jetpack Compose
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Configura el contenido de la actividad usando Jetpack Compose
        setContent {
            // Llama a la función App(), que define la interfaz principal de la aplicación
            App()
        }
    }
}
