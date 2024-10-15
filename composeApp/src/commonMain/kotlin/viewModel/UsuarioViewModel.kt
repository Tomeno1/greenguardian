package viewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.AuthService
import data.HttpClientProvider
import data.UserService
import kotlinx.coroutines.launch
import model.AuthUsuario
import model.EstanqueByUsuarioResponse
import model.ResponseHttp
import model.Usuario
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class UsuarioViewModel(private val tokenViewModel: TokenViewModel) : ViewModel() {
    init {
        Log.d("UsuarioViewModel", "UsuarioViewModel creado")
    }

    private val userService = UserService(HttpClientProvider.client)
    private val authService = AuthService(HttpClientProvider.client)

    var usuario: Usuario? by mutableStateOf(null)
        private set

    var usuarios: List<Usuario> by mutableStateOf(emptyList())
        private set

    var userImageUri: Uri? by mutableStateOf(null)
        private set

    // Lista de estanques relacionados con el usuario
    var estanquesByUsuario = mutableStateOf<EstanqueByUsuarioResponse?>(null)
        private set

    // Actualizar la URI de la imagen del usuario
    fun updateUserImageUri(newUri: Uri) {
        userImageUri = newUri
    }

    // Actualizar la información del usuario actual
    fun updateUser(newUsuario: Usuario) {
        usuario = newUsuario
    }

    // Iniciar sesión de usuario
    fun loginUser(authUsuario: AuthUsuario, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Llamada al servicio de autenticación
                val tokenResponse = authService.login(authUsuario)

                if (tokenResponse != null) {
                    Log.d("Login", "Token recibido: ${tokenResponse.token}")

                    // Validar token y obtener usuario
                    val validatedUser = authService.validateToken(ResponseHttp(tokenResponse.token))

                    if (validatedUser != null) {
                        // Actualizamos el usuario y el token en el ViewModel
                        usuario = validatedUser
                        Log.d("UsuarioViewModel", "Usuario logueado: $usuario")
                        tokenViewModel.updateToken(tokenResponse.token)

                        onSuccess()  // Llamamos a la función de éxito
                    } else {
                        onError("No se pudo validar el token o obtener el usuario")
                    }
                } else {
                    onError("Credenciales incorrectas")
                }
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Error durante el login: ${e.message}")
                onError("Error durante el inicio de sesión: ${e.message}")
            }
        }
    }

    // Cargar estanques de un usuario por ID con token de autenticación
    fun loadEstanquesByUsuario(userId: Long, onError: (String) -> Unit = {}) {
        val token = tokenViewModel.token

        if (token.isNullOrBlank()) {
            onError("Token de acceso inválido o no disponible")
            return
        }

        viewModelScope.launch {
            try {
                val response = userService.getEstanquesByUsuario(token, userId)
                if (response != null) {
                    estanquesByUsuario.value = response
                    Log.d("UsuarioViewModel", "Estanques cargados para el usuario $userId")
                } else {
                    onError("No se pudieron cargar los estanques del usuario")
                }
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Error al cargar los estanques: ${e.message}")
                onError("Error al cargar los estanques del usuario")
            }
        }
    }

    // Editar la información de un usuario
    fun editUser(usuario: Usuario, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val currentToken = tokenViewModel.token
            if (currentToken.isNullOrBlank()) {
                onError("No hay token de acceso")
                return@launch
            }

            try {
                val result = userService.updateUser(currentToken, usuario)
                if (result) {
                    usuarios = usuarios.map {
                        if (it.idUsuario == usuario.idUsuario) usuario else it
                    }
                    onSuccess()
                } else {
                    onError("No se pudo actualizar el usuario")
                }
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Error al actualizar el usuario: ${e.message}")
                onError("Error al actualizar el usuario")
            }
        }
    }

    // Eliminar un usuario
    fun deleteUser(userId: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val currentToken = tokenViewModel.token
            if (currentToken.isNullOrBlank()) {
                onError("No hay token de acceso")
                return@launch
            }

            try {
                val result = userService.deleteUser(currentToken, userId)
                if (result) {
                    usuarios = usuarios.filter { it.idUsuario != userId }
                    onSuccess()
                } else {
                    onError("No se pudo eliminar el usuario")
                }
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Error al eliminar el usuario: ${e.message}")
                onError("Error al eliminar el usuario")
            }
        }
    }
}
