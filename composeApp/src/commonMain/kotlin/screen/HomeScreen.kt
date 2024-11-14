import android.util.Log
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.AlertDialog
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.Card
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.WaterDrop
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import components.getImageResourceByName
import data.HydroponicGuideDataManager
import data.PlantDataManager
import model.GuideStepData
import model.Plant
import moe.tlaster.precompose.navigation.Navigator
import techminds.greenguardian.R
import viewModel.EstanqueViewModel
import viewModel.TokenViewModel
import viewModel.UsuarioViewModel
import java.util.Locale

@Composable
fun HomeScreen(
    navigator: Navigator,
    usuarioViewModel: UsuarioViewModel,
) {
    // Obtenemos el nombre y apellido del usuario del ViewModel de forma reactiva
    val userName by remember { mutableStateOf(usuarioViewModel.usuario?.nombre ?: "Invitado") }
    val lastName by remember { mutableStateOf(usuarioViewModel.usuario?.apellido ?: "Invitado") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
            .padding(16.dp)
    ) {
        // Tarjeta de bienvenida al usuario
        GreetingCard(userName = userName, lastName = lastName, usuarioViewModel = usuarioViewModel)

        // Lista vertical que contiene secciones como solución nutritiva, lista de plantas y guía hidroponica
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.Transparent)
        ) {
            item {
                SolucionNutritiva(usuarioViewModel)  // Muestra los datos de la solución nutritiva
            }
            item {
                PlantList(plants = PlantDataManager.plants, navigator = navigator)  // Muestra una lista de plantas
            }
            item {
                HydroponicGuide()  // Muestra una guía para hidroponía
            }
        }
    }
}

@Composable
fun SolucionNutritiva(usuarioViewModel: UsuarioViewModel) {
    // Carga de los datos de promedio de estanques al iniciar la pantalla
    LaunchedEffect(Unit) {
        usuarioViewModel.usuario?.let { usuario ->
            usuarioViewModel.loadPromedioEstanques(usuario.idUsuario) { errorMessage ->
                Log.e("HomeScreen", "Error al cargar los datos: $errorMessage")
            }
        }
    }

    // Obtiene el promedio de los estanques del ViewModel
    val promedioEstanques = usuarioViewModel.promedioEstanques

    // Si los datos están cargados correctamente, mostrar la información
    if (promedioEstanques != null) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween, // Alinea los textos a los extremos
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                modifier = Modifier.padding(vertical = 8.dp),
                text = "Solución Nutritiva",
                fontWeight = FontWeight.Bold,
                style = MaterialTheme.typography.h6
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(300.dp)
        ) {
            // Indicador semicircular para la temperatura del estanque
            Card(
                modifier = Modifier.padding(end = 8.dp),
                shape = RoundedCornerShape(16.dp),
                elevation = 4.dp,
            ) {
                Column(
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally,
                    modifier = Modifier
                        .fillMaxHeight()
                        .background(Color.Transparent)
                        .padding(8.dp)
                ) {
                    SemicircularProgressIndicator(
                        progress = promedioEstanques.temperature / 50f, // Asumiendo un rango de 0-50°C
                        value = String.format(Locale.US, "%.2f°C", promedioEstanques.temperature),
                        label = "Temperatura estanque"
                    )
                }
            }

            // PH y Conductividad en la columna derecha
            Column(
                modifier = Modifier.fillMaxHeight(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Muestra el nivel de PH
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .background(Color.Transparent)
                ) {
                    Card(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(bottom = 8.dp),
                        shape = RoundedCornerShape(16.dp),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "pH",
                                style = MaterialTheme.typography.subtitle1,
                                color = Color.Gray
                            )
                            Text(
                                text = String.format(Locale.US, "%.2f pH", promedioEstanques.ph),
                                style = MaterialTheme.typography.h6,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            // Línea que representa el nivel de PH
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                            ) {
                                val ph = promedioEstanques.ph
                                val lineEnd =
                                    (ph / 14f) * size.width // Suponiendo un rango de pH 0-14
                                drawLine(
                                    color = Color.Red,
                                    start = Offset(0f, size.height / 2),
                                    end = Offset(lineEnd, size.height / 2),
                                    strokeWidth = 8f
                                )
                            }
                        }
                    }
                }

                // Muestra el nivel de Conductividad
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    Card(
                        shape = RoundedCornerShape(16.dp),
                        modifier = Modifier.fillMaxSize(),
                        elevation = 4.dp
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(10.dp),
                            verticalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Conductividad",
                                style = MaterialTheme.typography.subtitle1,
                                color = Color.Gray
                            )
                            Text(
                                text = String.format(Locale.US, "%.2f µS/cm", promedioEstanques.ec),
                                style = MaterialTheme.typography.h6,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )
                            // Línea que representa el nivel de Conductividad
                            Canvas(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .height(40.dp)
                            ) {
                                // Suponiendo un rango máximo de conductividad (ejemplo: 0-2000 µS/cm)
                                val maxEC = 2000f
                                val ecValue = promedioEstanques.ec

                                // Normalizar el valor de EC en base al rango
                                val lineEnd = (ecValue / maxEC) * size.width

                                // Dibujar la línea proporcional al valor de EC
                                drawLine(
                                    color = Color(0xFF4CAF50),
                                    start = Offset(0f, size.height / 2),
                                    end = Offset(
                                        lineEnd,
                                        size.height / 2
                                    ),  // La longitud se ajusta al valor de EC
                                    strokeWidth = 8f
                                )
                            }
                        }
                    }
                }

            }
        }
    } else {
        // Mostrar mensaje de carga o de error
        Text(
            text = "Cargando datos de la solución nutritiva...",
            modifier = Modifier.padding(16.dp),
            color = Color.Gray
        )
    }
}


@Composable
fun SemicircularProgressIndicator(
    progress: Float,
    value: String,
    label: String
) {
    // Indicador semicircular para mostrar un valor con progreso visual
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(150.dp)
            .background(Color.Transparent)
            .padding(10.dp)
    ) {
        Canvas(modifier = Modifier.size(150.dp)) {  // Ajusta el tamaño del Canvas según tus necesidades
            // Arco de fondo
            drawArc(
                color = Color.LightGray,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(12.dp.toPx(), cap = StrokeCap.Round)
            )

            // Arco de progreso
            drawArc(
                color = Color(0xFF2196F3),
                startAngle = 180f,
                sweepAngle = 180f * progress,
                useCenter = false,
                style = Stroke(12.dp.toPx(), cap = StrokeCap.Round)
            )
        }

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(text = value, style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
            Text(text = label, style = MaterialTheme.typography.caption)
        }
    }
}

@Composable
fun GreetingCard(userName: String,lastName: String, usuarioViewModel: UsuarioViewModel) {
    // Muestra un saludo al usuario con su imagen de perfil si está disponible
    val imageUri = usuarioViewModel.userImageUri
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {


        // Mostrar la imagen seleccionada o la imagen predeterminada
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "User Profile Picture",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape) // Hace la imagen circular
                    .border(2.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop // Ajusta la imagen para llenar el círculo
            )
        } else {
            // Mostrar imagen predeterminada si no hay imagen seleccionada
            Image(
                painter = painterResource(R.drawable.greenguardian  ), // Imagen predeterminada de drawable
                contentDescription = "Default User Icon",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                contentScale = ContentScale.Crop
            )
        }

        Spacer(modifier = Modifier.width(12.dp)) // Espacio entre la imagen y el texto

        // Texto de saludo
        Column(
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Bienvenido,", // Texto de saludo
                style = MaterialTheme.typography.body2, // Puedes ajustar el estilo
                color = Color.Gray
            )
            Text(
                text = "$userName $lastName", // Nombre del usuario
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun HydroponicGuide() {
    // Muestra los pasos de una guía de hidroponía, con diálogo emergente al seleccionar un paso
    var selectedStep by remember { mutableStateOf<GuideStepData?>(null) }

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            modifier = Modifier.padding(vertical = 16.dp),
            text = "Guía para Cultivos Hidropónicos",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
        )

        // Usamos los pasos de la guía del DataManager
        HydroponicGuideDataManager.guideSteps.forEach { step ->
            GuideStep(
                title = step.title,
                icon = step.icon,
                iconTint = step.iconTint,
                onClick = { selectedStep = step }
            )
        }
    }
    // Diálogo para mostrar la descripción del paso seleccionado
    selectedStep?.let { step ->
        AlertDialog(
            onDismissRequest = { selectedStep = null },
            title = {
                Text(
                    text = step.title,
                    style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold),
                    color = Color(0xFF1A1A1A) // Color de título más oscuro
                )
            },
            text = {
                Column(
                    modifier = Modifier
                        .padding(vertical = 8.dp)
                        .fillMaxWidth()
                ) {
                    Text(
                        text = step.description,
                        style = MaterialTheme.typography.body1,
                        color = Color(0xFF4D4D4D) // Color de texto más tenue
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = { selectedStep = null },
                    colors = ButtonDefaults.buttonColors(backgroundColor = Color(0xFF38C93E)),
                    shape = RoundedCornerShape(8.dp), // Botón con bordes redondeados
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Text(
                        text = "Cerrar",
                        color = Color.White,
                        style = MaterialTheme.typography.button.copy(fontWeight = FontWeight.SemiBold)
                    )
                }
            },
            backgroundColor = Color.White,
            shape = RoundedCornerShape(12.dp), // Diálogo con bordes suavemente redondeados
            modifier = Modifier.padding(16.dp) // Espacio alrededor del diálogo
        )
    }
}


@Composable
fun GuideStep(
    title: String,
    icon: ImageVector,
    iconTint: Color,
    onClick: () -> Unit
) {
    // Paso de la guía que es clickeable y muestra un ícono
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(bottom = 8.dp)

    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.padding(16.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(40.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Text(
                text = title,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

@Composable
fun PlantList(plants: List<Plant>, navigator: Navigator) {
    // Lista horizontal de plantas con un botón "Ver más"
    Row(
        modifier = Modifier.padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            modifier = Modifier.weight(1f),
            text = "Plantas Comunes para Hidroponía",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
        )
        Button(
            shape = RoundedCornerShape(50),
            onClick = { navigator.navigate("/plantList") }, // Navegar a la lista completa
            colors = ButtonDefaults.buttonColors(backgroundColor = Color.LightGray)
        ) {
            Text(text = "Ver más", color = Color.DarkGray, fontWeight = FontWeight.Bold)
        }
    }

    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp)
    ) {
        items(plants.take(6)) { plant ->
            PlantCard(plant = plant) {
                navigator.navigate("/plantDetail/${plant.name}")
            }
        }
    }
}


@Composable
fun PlantCard(plant: Plant, onClick: () -> Unit) {
    // Tarjeta que representa una planta específica en la lista de plantas
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        modifier = Modifier
            .width(200.dp)
            .padding(vertical = 8.dp, horizontal = 8.dp)
            .clickable { onClick() }

    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = rememberAsyncImagePainter(plant.imageUrl),
                contentDescription = plant.name,
                modifier = Modifier
                    .height(100.dp),
                contentScale = ContentScale.Crop
            )
            Text(
                text = plant.name,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}

