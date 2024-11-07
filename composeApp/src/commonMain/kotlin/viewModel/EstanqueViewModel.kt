package viewModel

import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import service.EstanqueNoSQLService
import service.EstanqueService
import service.HttpClientProvider
import kotlinx.coroutines.launch
import model.Estanque
import model.EstanqueNoSQL
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class EstanqueViewModel(private val tokenViewModel: TokenViewModel) : ViewModel() {

    init {
        Log.d("EstanqueViewModel", "EstanqueViewModel creado")
    }

    // Servicios de estanques para realizar operaciones SQL y NoSQL
    private val estanqueService = EstanqueService(HttpClientProvider.client)
    private val estanqueNoSQLService = EstanqueNoSQLService(HttpClientProvider.client)

    // Lista de estanques observables (SQL)
    var estanques = mutableStateListOf<Estanque>()
        private set

    // Estado del estanque seleccionado (SQL)
    var selectedEstanque = mutableStateOf<Estanque?>(null)
        private set

    // Estado del estanque NoSQL seleccionado
    var selectedEstanqueNoSQL = mutableStateOf<EstanqueNoSQL?>(null)
        private set

    // Mensaje de error para operaciones NoSQL
    var errorMessage = mutableStateOf<String?>(null)
        private set

    // Función para cargar todos los estanques desde la base de datos SQL
    fun loadEstanques() {
        viewModelScope.launch {
            try {
                val result = estanqueService.getAllEstanques()
                if (result != null) {
                    estanques.clear() // Limpiar lista antes de agregar los resultados
                    estanques.addAll(result)
                }
            } catch (e: Exception) {
                Log.e("EstanqueViewModel", "Error al cargar los estanques: ${e.message}")
            }
        }
    }

    // Función para cargar un estanque específico por su ID desde la base de datos SQL
    fun loadEstanqueById(idEstanque: Long) {
        viewModelScope.launch {
            Log.d("EstanqueViewModel", "Iniciando carga de estanque con ID: $idEstanque")

            try {
                // Obtiene el token de autenticación desde el tokenViewModel
                val token = tokenViewModel.token
                if (token == null) {
                    Log.e("EstanqueViewModel", "Token no disponible")
                    return@launch
                }

                // Llama a la función del servicio SQL para obtener el estanque por ID
                val result = estanqueService.getEstanqueById(token, idEstanque)
                if (result != null) {
                    selectedEstanque.value = result
                    Log.d("EstanqueViewModel", "Estanque cargado: $result")
                } else {
                    Log.d("EstanqueViewModel", "No se encontró el estanque con ID: $idEstanque")
                }
            } catch (e: Exception) {
                Log.e("EstanqueViewModel", "Error al cargar el estanque: ${e.message}")
            }
        }
    }

    // Función para cargar el último estanque de la base de datos NoSQL usando su ID
    fun loadEstanqueNoSQLById(idEstanque: Int) {
        viewModelScope.launch {
            Log.d("EstanqueViewModel", "Iniciando carga de Estanque NoSQL con ID: $idEstanque")

            // Limpia el estado anterior del estanque y mensajes de error
            selectedEstanqueNoSQL.value = null
            errorMessage.value = null

            try {
                // Intenta obtener el último estanque NoSQL por ID
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

    // Función para crear un nuevo estanque en la base de datos SQL
    fun createEstanque(estanque: Estanque, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = estanqueService.createEstanque(estanque)
                if (result != null) {
                    estanques.add(result) // Añadir el estanque a la lista local
                    onSuccess() // Callback de éxito
                } else {
                    onError("No se pudo crear el estanque")
                }
            } catch (e: Exception) {
                Log.e("EstanqueViewModel", "Error al crear el estanque: ${e.message}")
                onError("Error al crear el estanque")
            }
        }
    }

    // Función para actualizar un estanque en la base de datos SQL por ID
    fun updateEstanque(idEstanque: Long, estanque: Estanque, onSuccess: () -> Unit, onError: (String) -> Unit) {
        val token = tokenViewModel.token
        viewModelScope.launch {
            try {
                val result = token?.let { estanqueService.updateEstanque(it, idEstanque, estanque) }
                if (result != null) {
                    val index = estanques.indexOfFirst { it.idEstanque == idEstanque }

                    if (index >= 0) {
                        estanques[index] = result // Actualizar el estanque en la lista local
                    }

                    // También actualizar el estanque seleccionado si coincide con el ID
                    if (selectedEstanque.value?.idEstanque == idEstanque) {
                        selectedEstanque.value = result
                    }

                    onSuccess() // Callback de éxito
                } else {
                    onError("No se pudo actualizar el estanque")
                }
            } catch (e: Exception) {
                Log.e("EstanqueViewModel", "Error al actualizar el estanque: ${e.message}")
                onError("Error al actualizar el estanque")
            }
        }
    }

    // Función para eliminar un estanque de la base de datos SQL
    fun deleteEstanque(idEstanque: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            try {
                val result = estanqueService.deleteEstanque(idEstanque)
                if (result) {
                    estanques.removeAll { it.idEstanque == idEstanque } // Remover el estanque de la lista local
                    onSuccess() // Callback de éxito
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
