package viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.ApiConnection
import kotlinx.coroutines.launch
import model.ResponseHttp
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class TokenViewModel : ViewModel() {

    // API connection instance
    private val apiConnection = ApiConnection()

    // Token value and logged-in status
    var token: String? by mutableStateOf(null)
        private set

    var isLoggedIn: Boolean by mutableStateOf(false)
        private set

    // Initialize the ViewModel and log its creation
    init {
        Log.d("TokenViewModel", "TokenViewModel creado")
    }

    // Function to update the token and set the logged-in status
    fun updateToken(newToken: String) {
        token = newToken
        isLoggedIn = true
        Log.d("TokenViewModel", "Token actualizado: $newToken")
    }

    // Function to validate the token with the server
    fun validateToken(response: ResponseHttp, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val validationResponse = apiConnection.validation(response)
                if (validationResponse != null) {
                    updateToken(response.accessToken) // Update the token if validation is successful
                    onSuccess()
                } else {
                    // Handle invalid token response
                    onError("Token inválido")
                    Log.d("TokenViewModel", "Token inválido: ${response.accessToken}")
                }
            } catch (e: Exception) {
                // Log and handle validation error
                onError("Error de validación")
                Log.d("TokenViewModel", "Error de validación", e)
            }
        }
    }

    // Function to handle the logout process
    fun logout(onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Clear the token and logged-in status
                clearToken()

                // If there is a logout endpoint in your API, call it here
                // For example:
                // apiConnection.logout(token)

                // Successfully logged out
                onSuccess()
                Log.d("TokenViewModel", "Sesión cerrada exitosamente")
            } catch (e: Exception) {
                // Log and handle logout error
                onError("Error al cerrar sesión")
                Log.d("TokenViewModel", "Error al cerrar sesión", e)
            }
        }
    }

    // Function to clear the token and reset logged-in status
    fun clearToken() {
        token = null
        isLoggedIn = false
        Log.d("TokenViewModel", "Token eliminado y sesión cerrada")
    }
}
