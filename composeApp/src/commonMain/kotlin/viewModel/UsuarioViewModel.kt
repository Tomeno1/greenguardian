package viewModel

import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.ApiConnection
import kotlinx.coroutines.launch
import model.ResponseHttp
import model.Usuario
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class UsuarioViewModel(private val tokenViewModel: TokenViewModel) : ViewModel() {
    init {
        Log.d("UsuarioViewModel", "UsuarioViewModel creado")
    }

    private val apiConnection = ApiConnection()

    var usuario: Usuario? by mutableStateOf(null)
        private set

    var usuarios: List<Usuario> by mutableStateOf(emptyList())
        private set

    var userImageUri: Uri? by mutableStateOf(null)
        private set

    // Actualizar la URI de la imagen del usuario
    fun updateUserImageUri(newUri: Uri) {
        userImageUri = newUri
    }

    // Actualizar la información del usuario actual
    fun updateUser(newUsuario: Usuario) {
        usuario = newUsuario
    }

    // Cargar la lista de usuarios desde la API
    fun loadUsers(onError: (String) -> Unit = {}) {
        viewModelScope.launch {
            val currentToken = tokenViewModel.token
            if (currentToken.isNullOrBlank()) {
                onError("No hay token de acceso disponible")
                return@launch
            }

            try {
                val users = apiConnection.getUsers(currentToken)
                usuarios = users ?: emptyList()
                if (users == null) {
                    onError("No se pudieron cargar los usuarios")
                }
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Error al cargar los usuarios: ${e.message}")
                onError("Error al cargar los usuarios")
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
                val result = apiConnection.updateUser(currentToken, usuario)
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
                val result = apiConnection.deleteUser(currentToken, userId)
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

    // Crear un nuevo usuario
    fun createUser(usuario: Usuario, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val currentToken = tokenViewModel.token
            if (currentToken.isNullOrBlank()) {
                onError("No hay token de acceso")
                return@launch
            }

            try {
                val result = apiConnection.createUser(usuario)
                if (result) {
                    usuarios = usuarios + usuario
                    onSuccess()
                } else {
                    onError("No se pudo crear el usuario")
                }
            } catch (e: Exception) {
                Log.e("UsuarioViewModel", "Error al crear el usuario: ${e.message}")
                onError("Error al crear el usuario")
            }
        }
    }

    // Validar al usuario usando la API
    fun validateUser(response: ResponseHttp, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                Log.d("Login", "Validando token: ${response.accessToken}")
                val validationResponse = apiConnection.validation(response)
                if (validationResponse != null) {
                    Log.d("Login", "Respuesta de validación: $validationResponse")
                    updateUser(validationResponse)
                    tokenViewModel.updateToken(response.accessToken)
                    onSuccess()
                } else {
                    Log.d("Login", "Respuesta de validación es nula")
                    onError("Usuario inválido")
                }
            } catch (e: Exception) {
                Log.e("Login", "Error durante la validación: ${e.message}")
                onError("Error de validación")
            }
        }
    }
}
