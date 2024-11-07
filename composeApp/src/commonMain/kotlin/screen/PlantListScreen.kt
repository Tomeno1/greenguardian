package screen

import PlantCard
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import data.PlantDataManager
import moe.tlaster.precompose.navigation.Navigator

@Composable
fun PlantListScreen(navigator: Navigator) {
    // Pantalla que muestra una lista completa de plantas en una cuadrícula adaptable
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Título de la pantalla
        Text(
            text = "Lista Completa de Plantas",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Cuadrícula adaptable para mostrar las tarjetas de plantas
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 150.dp),  // Tamaño mínimo para cada celda
            modifier = Modifier.fillMaxSize()
        ) {
            // Itera sobre la lista de plantas del PlantDataManager y crea una tarjeta por cada planta
            items(PlantDataManager.plants) { plant ->
                PlantCard(plant = plant) {
                    navigator.navigate("/plantDetail/${plant.name}")  // Navega a los detalles de la planta
                }
            }
        }
    }
}
