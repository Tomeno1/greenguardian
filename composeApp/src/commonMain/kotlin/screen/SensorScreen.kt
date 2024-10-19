package screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import viewModel.EstanqueViewModel

@Composable
fun SensorScreen(estanqueViewModel: EstanqueViewModel) {
    // Obtenemos el estado de los datos de sensores desde el ViewModel
    val estanqueNoSQL by estanqueViewModel.selectedEstanqueNoSQL

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Datos del estanque seleccionado", style = MaterialTheme.typography.h5)

        if (estanqueNoSQL != null) {
            LazyVerticalGrid(columns = GridCells.Fixed(2)) {
                item{
                    SensorDataItem(
                        label = "Temperatura",
                        value = estanqueNoSQL!!.deviceData.temperature,
                        imageName = "temperatura",
                        color = Color.Blue,
                        maxValue = 100
                    )
                }
                item{
                    SensorDataItem(
                        label = "Humedad",
                        value = estanqueNoSQL!!.deviceData.humidity,
                        imageName = "humedad",
                        color = Color.Green,
                        maxValue = 100
                    )
                }
            }

            // Puedes agregar más datos de sensores si es necesario
        } else {
            Text(text = "Cargando datos de sensores...")
        }
    }
}
