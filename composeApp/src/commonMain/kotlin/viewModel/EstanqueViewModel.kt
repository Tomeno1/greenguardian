package viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import data.EstanqueNoSQLService
import data.EstanqueService
import data.HttpClientProvider
import kotlinx.coroutines.launch
import model.Estanque
import model.EstanqueNoSQL
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class EstanqueViewModel(private val tokenViewModel: TokenViewModel) : ViewModel() {

    init {
        Log.d("EstanqueViewModel", "EstanqueViewModel creado")
    }

    private val estanqueService = EstanqueService(HttpClientProvider.client)
    private val estanqueNoSQLService = EstanqueNoSQLService(HttpClientProvider.client)

    // Lista de estanques observables
    var estanques = mutableStateListOf<Estanque>()
        private set

    // Estado del estanque seleccionado
    var selectedEstanque = mutableStateOf<Estanque?>(null)
        private set

    // Estado del estanque NoSQL
    var selectedEstanqueNoSQL = mutableStateOf<EstanqueNoSQL?>(null)
        private set

    // Error message for NoSQL
    var errorMessage = mutableStateOf<String?>(null)
        private set

    // Cargar estanques SQL
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

    // Cargar un estanque SQL por ID
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

    fun loadEstanqueNoSQLById(idEstanque: Int) {
        viewModelScope.launch {
            // Limpiar el estado anterior antes de cargar el nuevo estanque
            selectedEstanqueNoSQL.value = null
            errorMessage.value = null

            try {
                val result = estanqueNoSQLService.getUltimoEstanque(idEstanque)
                result.onSuccess { data ->
                    selectedEstanqueNoSQL.value = data
                    Log.d("EstanqueViewModel", "Estanque NoSQL cargado correctamente: ${data.idEstanque}, temperatura: ${data.deviceData.temperature}, humedad: ${data.deviceData.humidity}")
                }.onFailure { exception ->
                    errorMessage.value = exception.message
                    Log.e("EstanqueViewModel", "Error al cargar el estanque NoSQL: ${exception.message}")
                }
            } catch (e: Exception) {
                Log.e("EstanqueViewModel", "Error al cargar el estanque NoSQL: ${e.message}")
                errorMessage.value = e.message
            }
        }
    }


    // Crear un nuevo estanque SQL
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

    // Actualizar un estanque SQL
    fun updateEstanque(idEstanque: Long, estanque: Estanque, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val token = tokenViewModel.token
        viewModelScope.launch {
            try {
                val result = token?.let { estanqueService.updateEstanque(it, idEstanque, estanque) }
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

    // Eliminar un estanque SQL
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
