package viewModel

import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import data.ApiConnection
import kotlinx.coroutines.launch
import model.Estanque
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class EstanqueViewModel(private val token: TokenViewModel): ViewModel() {
    init {
        Log.d("EstanqueViewModel", "EstanqueViewModel creado")
    }
    private val apiConnection = ApiConnection()

    var estanques: List<Estanque> by mutableStateOf(emptyList())
        private set

    var selectedEstanque: Estanque? by mutableStateOf(null)
        private set

    fun selectEstanque(estanque: Estanque) {
        selectedEstanque = estanque
    }

    fun createEstanque(estanque: Estanque, userId: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val currentToken = token
            if (currentToken != null) {

                val result = apiConnection.createEstanque(estanque, userId)
                if (result) {
                    onSuccess()
                } else {
                    Log.d("SessionViewModel", "No se pudo crear el estanque")
                }
            } else {
                Log.d("SessionViewModel", "No hay token de acceso")
            }
        }
    }

    fun loadEstanques(userId: Long, onSuccess: (List<Estanque>) -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val currentToken = token
            if (currentToken != null) {
                val fetchedEstanques = apiConnection.getEstanques(userId)
                if (fetchedEstanques != null) {
                    estanques = fetchedEstanques
                    onSuccess(fetchedEstanques)
                } else {
                    Log.d("SessionViewModel", "No se pudieron cargar los estanques")
                    onError("No se pudieron cargar los estanques")
                    estanques = emptyList()
                }
            } else {
                Log.d("SessionViewModel", "No hay token de acceso")
                onError("No hay token de acceso")
            }
        }
    }

    fun deleteEstanque(estanqueId: Long, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val currentToken = token
            if (currentToken != null) {
                val result = apiConnection.deleteEstanque(estanqueId)
                if (result) {
                    onSuccess()
                } else {
                    onError("No se pudo eliminar el estanque")
                }
            } else {
                onError("No hay token de acceso")
            }
        }
    }

    fun updateEstanque(estanque: Estanque, onSuccess: () -> Unit, onError: (String) -> Unit) {
        viewModelScope.launch {
            val currentToken = token
            if (currentToken != null) {
                val result = apiConnection.updateEstanque(estanque)
                if (result) {
                    onSuccess()
                } else {
                    onError("No se pudo actualizar el estanque")
                }
            } else {
                onError("No hay token de acceso")
            }
        }
    }
}