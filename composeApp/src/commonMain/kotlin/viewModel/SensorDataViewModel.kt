package viewModel
/*
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import data.ApiConnection
import kotlinx.coroutines.launch
import model.SensorData
import moe.tlaster.precompose.viewmodel.ViewModel
import moe.tlaster.precompose.viewmodel.viewModelScope

class SensorDataViewModel : ViewModel() {
    init {
        Log.d("SensorDataViewModel", "SensorDataViewModel creado")
    }
    private val apiConnection = ApiConnection()
    var sensorData: SensorData? by mutableStateOf(null)
        private set
    var isLoading: Boolean by mutableStateOf(false)
        private set
    var errorMessage: String? by mutableStateOf(null)
        private set
    fun fetchSensorData() {
        viewModelScope.launch {
            isLoading = true
            errorMessage = null
            try {
                val data = apiConnection.getSensorData()
                if (data != null) {
                    sensorData = data
                } else {
                    errorMessage = "Failed to fetch sensor data"
                }
            } catch (e: Exception) {
                errorMessage = "Error: ${e.message}"
            } finally {
                isLoading = false
            }
        }
    }
}
*/
