package screen

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Thermostat
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Thermostat
import androidx.compose.material.icons.outlined.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import model.Plant
import techminds.greenguardian.R


@Composable
fun PlantDetailScreen(plant: Plant, onBack: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Imagen de la planta
        Image(
            painter = rememberAsyncImagePainter(plant.imageUrl),
            contentDescription = plant.name,
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp)
                .clip(RoundedCornerShape(12.dp)),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(16.dp))

        // Card con fondo blanco que encapsula la información de la planta
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            backgroundColor = Color.White,
            elevation = 4.dp
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Encabezado: Nombre y etiqueta de tipo de cultivo
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = plant.name,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.h4,
                        modifier = Modifier.weight(1f)
                    )
                    Text(
                        text = "Hidropónico",
                        color = Color.Black,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        modifier = Modifier
                            .background(Color(0xFFE0E0E0), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Descripción de la planta
                Text(
                    text = plant.description,
                    style = MaterialTheme.typography.body1,
                    color = Color.Black
                )

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Eco, // Puedes usar otro icono si lo prefieres
                        contentDescription = "Icono de recomendaciones",
                        tint = Color.Green,
                        modifier = Modifier.size(30.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp)) // Espacio entre el icono y el texto
                    Text(
                        text = "Recomendaciones para cultivo hidropónico",
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.body1
                    )
                }

                Text(
                    text = plant.recommendations ?: "No disponible",
                    style = MaterialTheme.typography.body1,
                    color = Color.Black,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Sección de Niveles de pH y Temperatura óptima
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.WaterDrop,
                                contentDescription = "Icono de nivel de pH",
                                tint = Color.Blue,
                                modifier = Modifier.size(30.dp) // Tamaño del icono
                            )
                            Spacer(modifier = Modifier.width(4.dp)) // Espacio entre el icono y el texto
                            Text(
                                text = "Nivel de pH",
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        Text(
                            text = "${plant.phMin} - ${plant.phMax} pH",
                            style = MaterialTheme.typography.body1,
                            color = Color.Black
                        )
                    }

                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Thermostat,
                                contentDescription = "Icono de temperatura óptima",
                                tint = Color.Red,
                                modifier = Modifier.size(30.dp) // Tamaño del icono
                            )
                            Spacer(modifier = Modifier.width(4.dp)) // Espacio entre el icono y el texto
                            Text(
                                text = "Temperatura óptima",
                                style = MaterialTheme.typography.body1,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                        }
                        Text(
                            text = "${plant.tempMin}°C - ${plant.tempMax}°C",
                            style = MaterialTheme.typography.body1,
                            color = Color.Black
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Botón de regreso
        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.End) {
            Button(
                onClick = onBack,
                colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF38D13F))
            ) {
                Text(color = Color.White, text = "Volver")
            }
        }
    }
}


@Preview
@Composable
fun PlantDetailScreenPreview() {
    PlantDetailScreen(
        plant = Plant(
            name = "Lechuga",
            description = "Lechuga verde",
            imageUrl = R.drawable.lechuga,
            phMin = 6.0,
            phMax = 8.0,
            tempMin = 20,
            tempMax = 25,
            recommendations = "Recomendamos regar en verano"
        ), onBack = {})
}

