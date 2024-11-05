package viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.launch
import model.Tarea
import service.TareaService
import service.HttpClientProvider
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class TareaViewModel(private val tokenViewModel: TokenViewModel) : ViewModel() {

    private val tareaService = TareaService(HttpClientProvider.client)

    // Lista de tareas observables
     var tareas = mutableStateListOf<Tarea>()
        private set

    // Estado de la tarea seleccionada
     var selectedTarea = mutableStateOf<Tarea?>(null)
        private set

    // Estado de mensaje de error
     var errorMessage = mutableStateOf<String?>(null)
        private set

    // Cargar todas las tareas
    fun loadTareas() {
        viewModelScope.launch {
            try {
                val token = tokenViewModel.token
                val result = token?.let { tareaService.getAllTareas(it) }
                if (result != null) {
                    tareas.clear()
                    tareas.addAll(result)
                } else {
                    errorMessage.value = "No se pudieron cargar las tareas"
                }
            } catch (e: Exception) {
                Log.e("TareaViewModel", "Error al cargar tareas: ${e.message}")
                errorMessage.value = "Error al cargar las tareas"
            }
        }
    }

    // Cargar una tarea por ID
    fun loadTareaById(idTarea: Long) {
        viewModelScope.launch {
            try {
                val token = tokenViewModel.token
                val result = token?.let { tareaService.getTareaById(it, idTarea) }
                if (result != null) {
                    selectedTarea.value = result
                } else {
                    errorMessage.value = "No se encontró la tarea con ID: $idTarea"
                }
            } catch (e: Exception) {
                Log.e("TareaViewModel", "Error al cargar tarea: ${e.message}")
                errorMessage.value = "Error al cargar la tarea"
            }
        }
    }

    // Crear una nueva tarea
    fun createTarea(tarea: Tarea, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val token = tokenViewModel.token
                val result = token?.let { tareaService.createTarea(it, tarea) }
                if (result != null) {
                    tareas.add(result)
                    onSuccess()
                } else {
                    onError("No se pudo crear la tarea")
                }
            } catch (e: Exception) {
                Log.e("TareaViewModel", "Error al crear la tarea: ${e.message}")
                onError("Error al crear la tarea")
            }
        }
    }

    // Actualizar una tarea por ID
    fun updateTarea(idTarea: Long, tarea: Tarea, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val token = tokenViewModel.token
                val result = token?.let { tareaService.updateTarea(it, idTarea, tarea) }
                if (result != null) {
                    val index = tareas.indexOfFirst { it.id == idTarea }
                    if (index >= 0) {
                        tareas[index] = result
                    }
                    selectedTarea.value = if (selectedTarea.value?.id == idTarea) result else selectedTarea.value
                    onSuccess()
                } else {
                    onError("No se pudo actualizar la tarea")
                }
            } catch (e: Exception) {
                Log.e("TareaViewModel", "Error al actualizar la tarea: ${e.message}")
                onError("Error al actualizar la tarea")
            }
        }
    }

    // Eliminar una tarea por ID
    fun deleteTarea(idTarea: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val token = tokenViewModel.token
                val result = token?.let { tareaService.deleteTarea(it, idTarea) }
                if (result == true) {
                    tareas.removeAll { it.id == idTarea }
                    onSuccess()
                } else {
                    onError("No se pudo eliminar la tarea")
                }
            } catch (e: Exception) {
                Log.e("TareaViewModel", "Error al eliminar la tarea: ${e.message}")
                onError("Error al eliminar la tarea")
            }
        }
    }

    // Cargar tareas por ID de usuario
    fun loadTareasByIdUsuario(idUsuario: Long) {
        viewModelScope.launch {
            try {
                val token = tokenViewModel.token
                val result = token?.let { tareaService.getTareasByIdUsuario(it, idUsuario) }
                if (result != null) {
                    tareas.clear()
                    tareas.addAll(result)
                } else {
                    errorMessage.value = "No se encontraron tareas para el usuario con ID: $idUsuario"
                }
            } catch (e: Exception) {
                Log.e("TareaViewModel", "Error al cargar tareas por ID de usuario: ${e.message}")
                errorMessage.value = "Error al cargar tareas para el usuario"
            }
        }
    }
}
