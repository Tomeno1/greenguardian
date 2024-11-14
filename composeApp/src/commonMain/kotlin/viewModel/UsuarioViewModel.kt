package viewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import service.AuthService
import service.HttpClientProvider
import service.UserService
import kotlinx.coroutines.launch
import model.AuthUsuario
import model.EstanqueByUsuarioResponse
import model.PromedioEstanques
import model.ResponseHttp
import model.Usuario
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

// ViewModel que gestiona el estado del usuario y funciones relacionadas
class UsuarioViewModel(private val tokenViewModel: TokenViewModel) : ViewModel() {

    // Inicialización y registro del ViewModel
    init {
        Log.d("UsuarioViewModel", "UsuarioViewModel creado")
    }

    // Servicios de usuario y autenticación para el ViewModel
    private val userService = UserService(HttpClientProvider.client)
    private val authService = AuthService(HttpClientProvider.client)

    // Estado del usuario actualmente autenticado
    var usuario: Usuario? by mutableStateOf(null)
        private set

    // Lista de todos los usuarios (por ejemplo, para administradores)
    var usuarios: List<Usuario> by mutableStateOf(emptyList())
        private set

    // URI de la imagen de perfil del usuario
    var userImageUri: Uri? by mutableStateOf(null)
        private set

    // Estado de los estanques del usuario
    var estanquesByUsuario = mutableStateOf<EstanqueByUsuarioResponse?>(null)
        private set

    // Función para actualizar la URI de la imagen de perfil
    fun updateUserImageUri(newUri: Uri) {
        userImageUri = newUri
    }

    // Función para limpiar la imagen de perfil
    fun clearUserImage() {
        userImageUri = null // Limpia la imagen del usuario
    }

    // Variable que almacena el promedio de los valores de los estanques del usuario
    var promedioEstanques by mutableStateOf<PromedioEstanques?>(null)
        private set

    // Función para actualizar la información del usuario actual en el estado del ViewModel
    fun updateUser(newUsuario: Usuario) {
        usuario = newUsuario
    }

    // Función para iniciar sesión
    fun loginUser(authUsuario: AuthUsuario, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                // Solicita un token de autenticación
                val tokenResponse = authService.login(authUsuario)

                if (tokenResponse != null) {
                    Log.d("Login", "Token recibido: ${tokenResponse.token}")

                    // Valida el token y obtiene la información del usuario
                    val validatedUser = authService.validateToken(ResponseHttp(tokenResponse.token))

                    if (validatedUser != null) {
                        usuario = validatedUser
                        Log.d("UsuarioViewModel", "Usuario logueado: $usuario")
                        tokenViewModel.updateToken(tokenResponse.token)
                        onSuccess()  // Llama al callback de éxito
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

    fun loadEstanquesByUsuario(userId: Long, onError: (String) -> Unit = {}) {
        val token = tokenViewModel.token

        // Verifica si el token es válido
        if (token.isNullOrBlank()) {
            onError("Token de acceso inválido o no disponible")
            return
        }

        viewModelScope.launch {
            // Limpiar datos de estanques anteriores antes de cargar nuevos
            estanquesByUsuario.value = null

            try {
                val response = userService.getEstanquesByUsuario(token, userId)
                if (response != null) {
                    estanquesByUsuario.value = response
                    Log.d("UsuarioViewModel", "Estanques cargados para el usuario $userId")
                } else {
                    Log.e("UsuarioViewModel", "La respuesta de estanques es nula para el usuario $userId")
                    onError("No se pudieron cargar los estanques del usuario")
                }
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Error al cargar los estanques: ${e.message}")
                onError("Error al cargar los estanques del usuario")
            }
        }
    }


    // Función para cargar el promedio de los estanques
    fun loadPromedioEstanques(userId: Long, onError: (String) -> Unit = {}) {
        val token = tokenViewModel.token

        // Verifica si el token es válido
        if (token.isNullOrBlank()) {
            onError("Token de acceso inválido o no disponible")
            return
        }

        viewModelScope.launch {
            try {
                val response = userService.getPromedioEstanques(token, userId)
                if (response != null) {
                    promedioEstanques = response
                    Log.d("UsuarioViewModel", "Promedio de estanques cargado para el usuario $userId")
                } else {
                    onError("No se pudo obtener el promedio de los estanques")
                }
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Error al obtener el promedio de estanques: ${e.message}")
                onError("Error al obtener el promedio de estanques")
            }
        }
    }

    // Función para editar información de un usuario
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
                    // Actualiza la lista de usuarios
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

    // Función para eliminar un usuario
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
