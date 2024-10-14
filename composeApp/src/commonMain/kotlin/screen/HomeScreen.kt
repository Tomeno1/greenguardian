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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.rememberAsyncImagePainter
import coil.compose.rememberImagePainter
import model.GuideStepData
import model.Plant
import moe.tlaster.precompose.navigation.Navigator
import viewModel.TokenViewModel
import viewModel.UsuarioViewModel

@Composable
fun HomeScreen(navigator: Navigator,usuarioViewModel: UsuarioViewModel) {
    // Obtenemos el nombre del usuario del ViewModel de forma reactiva
    val userName by remember { mutableStateOf(usuarioViewModel.usuario?.nombre ?: "Invitado") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Transparent)
    ) {

        GreetingCard(userName = userName, usuarioViewModel = usuarioViewModel)
        SolucionNutritiva()
        PlantApp(navigator)
        HydroponicGuide()
    }
}

@Composable
fun SolucionNutritiva() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween, // Alinea los textos a los extremos
        verticalAlignment = Alignment.CenterVertically

    ) {
        Text(
            text = "Solución Nutritiva",
            fontWeight = FontWeight.Bold,
            style = MaterialTheme.typography.h6
        )
        Text(
            text = "Última actualización: 30 Dic. 10:35",
            style = MaterialTheme.typography.body2,
            color = Color.Gray // Puedes ajustar el color si prefieres algo más suave
        )
    }
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp)
            .padding(8.dp)
    ) {
        // Columna 1: Indicador semicircular (izquierda)

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
                // Indicador semicircular
                SemicircularProgressIndicator(
                    progress = 0.5f,
                    value = "200",
                    temperature = "25°",
                    label = "Temperatura estanque"
                )
            }
        }
        // Columna 2: PH y Conductividad (derecha)
        Column(
            modifier = Modifier
                .fillMaxHeight(),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Componente PH
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
                            text = "Ph",
                            style = MaterialTheme.typography.subtitle1,
                            color = Color.Gray
                        )
                        Text(
                            text = "6.3ph",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            drawLine(
                                color = Color.Red,
                                start = Offset(0f, size.height / 2),
                                end = Offset(size.width, size.height / 3),
                                strokeWidth = 8f
                            )
                        }
                    }
                }
            }
            // Componente Conductividad
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
                            text = "760 ppm",
                            style = MaterialTheme.typography.h6,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                        Canvas(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(40.dp)
                        ) {
                            drawLine(
                                color = Color(0xFF4CAF50),
                                start = Offset(0f, size.height / 2),
                                end = Offset(size.width, size.height / 3),
                                strokeWidth = 8f
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun SemicircularProgressIndicator(
    progress: Float,
    value: String,
    temperature: String,
    label: String
) {
    Box(
        contentAlignment = Alignment.Center,
        modifier = Modifier
            .size(150.dp)
            .background(Color.Transparent)
            .padding(10.dp)
    ) {
        Canvas(modifier = Modifier.size(150.dp)) {  // Ajusta el tamaño del Canvas según tus necesidades
            // Fondo del arco
            drawArc(
                color = Color.LightGray,
                startAngle = 180f,
                sweepAngle = 180f,
                useCenter = false,
                style = Stroke(12.dp.toPx(), cap = StrokeCap.Round)
            )

            // Arco con el progreso
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
            Text(text = temperature, style = MaterialTheme.typography.body2)
            Text(text = label, style = MaterialTheme.typography.caption)
        }
    }
}

@Composable
fun GreetingCard(userName: String, usuarioViewModel: UsuarioViewModel) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Observamos el estado de la URI de la imagen en el ViewModel
        val imageUri by remember { mutableStateOf(usuarioViewModel.userImageUri) }

        // Mostrar la imagen seleccionada o el ícono predeterminado
        if (imageUri != null) {
            Image(
                painter = rememberAsyncImagePainter(imageUri),
                contentDescription = "User Profile Picture",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape) // Hace la imagen circular
                    .border(2.dp, Color.Gray, CircleShape),
                contentScale = ContentScale.Crop // Borde blanco alrededor de la imagen
            )
        } else {
            // Mostrar ícono predeterminado si no hay imagen seleccionada
            Icon(
                imageVector = Icons.Default.AccountCircle,
                contentDescription = "User Icon",
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .border(2.dp, Color.White, CircleShape),
                tint = Color.White
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
                text = userName, // Nombre del usuario
                style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold)
            )
        }
    }
}

@Composable
fun HydroponicGuide() {
    val guideSteps = listOf(
        GuideStepData(
            stepNumber = 1,
            title = "Preparación del Sistema",
            description = "Elige un sistema hidropónico adecuado para el tipo de plantas que deseas cultivar. Los sistemas populares incluyen el de flujo y reflujo, NFT (película de nutrientes) y el de raíz flotante.",
            icon = Icons.Default.Build,
            iconTint = Color(0xFF3F51B5) // Azul
        ),
        GuideStepData(
            stepNumber = 2,
            title = "Selección de Plantas",
            description = "Selecciona plantas que se adapten bien a la hidroponía, como lechugas, espinacas, fresas o hierbas aromáticas. Asegúrate de que las semillas sean de buena calidad.",
            icon = Icons.Default.Eco,
            iconTint = Color(0xFF4CAF50) // Verde
        ),
        GuideStepData(
            stepNumber = 3,
            title = "Preparar la Solución Nutritiva",
            description = "Mezcla la solución nutritiva siguiendo las instrucciones del fabricante, ajustando el pH al nivel adecuado (generalmente entre 5.5 y 6.5). La concentración de nutrientes también debe ser controlada.",
            icon = Icons.Default.WaterDrop,
            iconTint = Color(0xFF2196F3) // Azul
        ),
        GuideStepData(
            stepNumber = 4,
            title = "Monitoreo y Mantenimiento",
            description = "Verifica regularmente el pH, la conductividad eléctrica (EC) y la temperatura del agua. Asegúrate de limpiar el sistema y cambiar la solución nutritiva periódicamente.",
            icon = Icons.Default.CheckCircle,
            iconTint = Color(0xFFFFA000) // Naranja
        )
    )

    var selectedStep by remember { mutableStateOf<GuideStepData?>(null) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        Text(
            text = "Guía para Cultivos Hidropónicos",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
        )
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(guideSteps) { step ->
            GuideStep(
                title = step.title,
                icon = step.icon,
                iconTint = step.iconTint,
                onClick = { selectedStep = step }
            )
        }
    }

    // Mostrar el AlertDialog si hay un paso seleccionado
    selectedStep?.let { step ->
        AlertDialog(
            onDismissRequest = { selectedStep = null },
            title = {
                Text(text = step.title)
            },
            text = {
                Text(text = step.description)
            },
            confirmButton = {
                Button(onClick = { selectedStep = null }) {
                    Text("Cerrar")
                }
            }
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

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Plantas comunes para Hidroponía",
            style = MaterialTheme.typography.h6,
            fontWeight = FontWeight.Bold,
        )
        Text(
            text = "Ver más",
            style = MaterialTheme.typography.body1,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.clickable { navigator.navigate("/Plantas") }
        )
    }
    LazyRow(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
    ) {
        items(plants) { plant ->
            PlantCard(plant = plant)
        }
    }
}

@Composable
fun PlantCard(plant: Plant) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        modifier = Modifier
            .width(200.dp)
            .padding(end = 8.dp)
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

@Composable
fun PlantApp(navigator: Navigator) {
    // Mostrar la lista de plantas
    val plants = listOf(
        Plant(
            name = "Lechuga",
            description = "La lechuga es ideal para sistemas hidropónicos debido a su rápido crecimiento y facilidad de cultivo.",
            imageUrl = "https://png.pngtree.com/png-clipart/20201208/original/pngtree-a-ripe-green-lettuce-png-image_5508901.jpg"
        ),
        Plant(
            name = "Albahaca",
            description = "La albahaca es una planta aromática popular en la hidroponía, utilizada para condimentar alimentos.",
            imageUrl = "https://ewpszg5hrx3.exactdn.com/wp-content/uploads/2021/01/Albahaca.png?strip=all&lossy=1&ssl=1"
        ),
        Plant(
            name = "Cilantro",
            description = "El cilantro es una planta aromática ampliamente utilizada en la hidroponía para condimentar alimentos.",
            imageUrl = "https://static.vecteezy.com/system/resources/previews/031/760/209/non_2x/coriander-with-ai-generated-free-png.png"
        ),
        Plant(
            name = "Cebolla",
            description = "La cebolla es una planta aromática utilizada en la hidroponía para condimentar alimentos.",
            imageUrl = "https://www.frutasbosquemar.cl/wp-content/uploads/2020/06/580b57fcd9996e24bc43c21d.png"
        ),
        Plant(
            name = "Espinaca",
            description = "La espinaca es una planta aromática utilizada en la hidroponía para condimentar alimentos.",
            imageUrl = "https://static.vecteezy.com/system/resources/previews/035/594/780/non_2x/ai-generated-fresh-spinach-leaves-isolated-on-transparent-background-free-png.png"
        ),
        Plant(
            name = "Tomate",
            description = "El tomate es una planta aromática utilizada en la hidroponía para condimentar alimentos. ",
            imageUrl = "https://static.vecteezy.com/system/resources/previews/028/882/790/original/tomato-tomato-red-tomato-with-transparent-background-ai-generated-free-png.png"
        ),



        // Agrega más plantas aquí
    )
    PlantList(plants = plants, navigator = navigator)
}

@Preview(showBackground = true)
@Composable
fun PreviewSimpleDashboardLayout() {
    GreetingCard("prueba", UsuarioViewModel(tokenViewModel = TokenViewModel()))

}


