package viewModel

import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import kotlinx.coroutines.launch
import model.Estado
import model.Estanque
import model.EstanqueNoSQL
import model.MessageHorarioRiego
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope
import service.EstanqueNoSQLService
import service.EstanqueService
import service.HttpClientProvider
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class EstanqueViewModel(private val tokenViewModel: TokenViewModel, private val mqttViewModel: MqttViewModel) : ViewModel() {

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

    var isLoading = mutableStateOf(true)


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
                    Log.d(
                        "EstanqueViewModel",
                        "Estanque NoSQL cargado correctamente: ${data.idEstanque}, temperatura: ${data.deviceData.temperature}, humedad: ${data.deviceData.humidity}"
                    )
                }.onFailure { exception ->
                    errorMessage.value = exception.message
                    Log.e(
                        "EstanqueViewModel",
                        "Error al cargar el estanque NoSQL: ${exception.message}"
                    )
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
    fun updateEstanque(
        idEstanque: Long,
        estanque: Estanque,
        onSuccess: () -> Unit,
        onError: (String) -> Unit
    ) {
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

    // Función para convertir el rango en un ClosedRange de flotantes
    fun parseRange(range: String): ClosedRange<Float> {
        val rangeParts = range.split("-")
        val min = rangeParts.getOrNull(0)?.toFloatOrNull() ?: 0f
        val max = rangeParts.getOrNull(1)?.toFloatOrNull() ?: 100f
        return min..max
    }

    // Función en EstanqueViewModel para determinar el estado del estanque
    fun determineEstanqueEstado(estanqueNoSQL: EstanqueNoSQL?, estanqueConfig: Estanque?): Estado {
        if (estanqueNoSQL == null || estanqueConfig == null) {
            return Estado.CARGANDO_ESTADO // Estado de advertencia si falta algún dato
        }

        val deviceData = estanqueNoSQL.deviceData
        val fueraDeRango = listOf(
            deviceData.temperature !in parseRange(estanqueConfig.rangoTemp ?: "0-100"),
            deviceData.humidity !in parseRange(estanqueConfig.rangoHum ?: "0-100"),
            deviceData.ec !in parseRange(estanqueConfig.rangoEc ?: "1.2-2.2"),
            deviceData.ph !in parseRange(estanqueConfig.rangoPh ?: "0-14"),
            deviceData.ldr !in parseRange(estanqueConfig.rangoLuz ?: "0-100")
        )

        return when {
            fueraDeRango.all { !it } -> Estado.BUEN_ESTADO // Todos los valores están dentro de rango
            fueraDeRango.any { it } -> Estado.ADVERTENCIA // Alguno está fuera de rango
            else -> Estado.MAL_ESTADO // Todos los valores están fuera de rango
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    fun publishIrrigationSchedule(estanque: Estanque) {
        val horaActual = LocalTime.now().format(DateTimeFormatter.ofPattern("HH:mm"))
        val message = MessageHorarioRiego(
            message = "Horarios de riego",
            hora_actual = horaActual,
            rango_horario = estanque.horarioRiego
        )
        mqttViewModel.publishMessageRiego("esp32/1/sub", message, {
            Log.d("EstanqueViewModel", "Mensaje de riego publicado correctamente")
        }, {
            Log.e("EstanqueViewModel", "Error al publicar el mensaje de riego: $it")
        })
    }


}
