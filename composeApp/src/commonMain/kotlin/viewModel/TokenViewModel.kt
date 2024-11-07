package viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import service.AuthService
import service.HttpClientProvider
import kotlinx.coroutines.launch
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

// ViewModel que maneja el token de autenticación y el estado de inicio de sesión del usuario
class TokenViewModel : ViewModel() {

    // Instancia del servicio de autenticación, configurado con el cliente HTTP
    private val authService = AuthService(HttpClientProvider.client)

    // Token de autenticación, observable en la UI y estado de inicio de sesión
    var token: String? by mutableStateOf(null)
        private set  // Solo se puede modificar dentro del ViewModel

    var isLoggedIn: Boolean by mutableStateOf(false)
        private set  // El estado de inicio de sesión es de solo lectura desde fuera del ViewModel

    // Inicialización del ViewModel con un registro en el log
    init {
        Log.d("TokenViewModel", "TokenViewModel creado")
    }

    // Función para actualizar el token y cambiar el estado a "logueado"
    fun updateToken(newToken: String) {
        token = newToken       // Asigna el nuevo token
        isLoggedIn = true      // Cambia el estado a logueado
        Log.d("TokenViewModel", "Token actualizado: $newToken")
    }

    // Función para gestionar el proceso de cierre de sesión
    fun logout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Llama a `clearToken` para limpiar el token y el estado de sesión
                clearToken()

                // Si tu API tiene un endpoint de logout, aquí se llamaría
                // Por ejemplo:
                // authService.logout(token)

                // Indica que el cierre de sesión fue exitoso
                onSuccess()
                Log.d("TokenViewModel", "Sesión cerrada exitosamente")
            } catch (e: Exception) {
                // En caso de error, llama a `onError` y registra el error
                onError("Error al cerrar sesión")
                Log.d("TokenViewModel", "Error al cerrar sesión", e)
            }
        }
    }

    // Función para limpiar el token y reiniciar el estado de sesión
    fun clearToken() {
        token = null            // Elimina el valor del token
        isLoggedIn = false      // Cambia el estado de sesión a no logueado
        Log.d("TokenViewModel", "Token eliminado y sesión cerrada")
    }
}
