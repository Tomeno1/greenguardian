package viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import data.EstanqueService
import data.HttpClientProvider
import kotlinx.coroutines.launch
import model.Estanque
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class EstanqueViewModel(private val token: TokenViewModel): ViewModel() {
    init {
        Log.d("EstanqueViewModel", "EstanqueViewModel creado")
    }
    private val estanqueService = EstanqueService(HttpClientProvider.client)

    // Lista de estanques observables
    var estanques = mutableStateListOf<Estanque>()
        private set

    // Estado del estanque seleccionado
    var selectedEstanque = mutableStateOf<Estanque?>(null)
        private set

    fun loadEstanques() {
        viewModelScope.launch {
            try {
                val result = estanqueService.getAllEstanques()
                if (result != null) {
                    estanques.clear()
                    estanques.addAll(result)
                }
            } catch (e: Exception) {
                Log.e("EstanqueViewModel", "Error al cargar los estanques: ${e.message}")
            }
        }
    }

    // Cargar un estanque por ID
    fun loadEstanqueById(idEstanque: Long) {
        viewModelScope.launch {
            try {
                val result = estanqueService.getEstanqueById(idEstanque)
                if (result != null) {
                    selectedEstanque.value = result
                }
            } catch (e: Exception) {
                Log.e("EstanqueViewModel", "Error al cargar el estanque: ${e.message}")
            }
        }
    }

    // Crear un nuevo estanque
    fun createEstanque(estanque: Estanque, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = estanqueService.createEstanque(estanque)
                if (result != null) {
                    estanques.add(result)
                    onSuccess()
                } else {
                    onError("No se pudo crear el estanque")
                }
            } catch (e: Exception) {
                Log.e("EstanqueViewModel", "Error al crear el estanque: ${e.message}")
                onError("Error al crear el estanque")
            }
        }
    }

    // Actualizar un estanque
    fun updateEstanque(idEstanque: Long, estanque: Estanque, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = estanqueService.updateEstanque(idEstanque, estanque)
                if (result != null) {
                    val index = estanques.indexOfFirst { it.idEstanque == idEstanque }
                    if (index >= 0) {
                        estanques[index] = result
                    }
                    onSuccess()
                } else {
                    onError("No se pudo actualizar el estanque")
                }
            } catch (e: Exception) {
                Log.e("EstanqueViewModel", "Error al actualizar el estanque: ${e.message}")
                onError("Error al actualizar el estanque")
            }
        }
    }

    // Eliminar un estanque
    fun deleteEstanque(idEstanque: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = estanqueService.deleteEstanque(idEstanque)
                if (result) {
                    estanques.removeAll { it.idEstanque == idEstanque }
                    onSuccess()
                } else {
                    onError("No se pudo eliminar el estanque")
                }
            } catch (e: Exception) {
                Log.e("EstanqueViewModel", "Error al eliminar el estanque: ${e.message}")
                onError("Error al eliminar el estanque")
            }
        }
    }
}