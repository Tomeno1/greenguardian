import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import model.Tarea
import viewModel.TareaViewModel
import viewModel.TokenViewModel

@Composable
fun TareasPendientesScreen(
    tareaViewModel: TareaViewModel,
    userId: Long // ID de usuario que se pasará desde el nivel superior
) {
    // Variables de estado para el nombre y la descripción de una nueva tarea, y el filtro de tareas
    var nuevaTareaNombre by remember { mutableStateOf("") }
    var nuevaTareaDescripcion by remember { mutableStateOf("") }
    var filtroSeleccionado by remember { mutableStateOf("Todas") }

    // Carga de tareas del usuario al inicializar la pantalla
    LaunchedEffect(userId) {
        tareaViewModel.loadTareasByIdUsuario(userId)
    }

    val tareas = tareaViewModel.tareas  // Lista de tareas cargadas desde el ViewModel

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        Card(
            shape = RoundedCornerShape(16.dp), // Bordes redondeados
            backgroundColor = MaterialTheme.colors.surface,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            elevation = 4.dp
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                // Encabezado de la sección de tareas
                Text("Tareas Pendientes", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                Text(
                    "Gestiona las tareas de tu cultivo hidropónico",
                    style = MaterialTheme.typography.body2,
                    color = Color.Gray
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Formulario para añadir una nueva tarea
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = nuevaTareaNombre,
                        onValueChange = { nuevaTareaNombre = it },
                        label = { Text("Nueva Tarea") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF38C93E),
                            unfocusedBorderColor = Color(0xFFB0BEC5),
                            cursorColor = Color(0xFF38C93E),
                            focusedLabelColor = Color(0xFF38C93E),
                            unfocusedLabelColor = Color(0xFF757575)
                        )
                    )

                    OutlinedTextField(
                        value = nuevaTareaDescripcion,
                        onValueChange = { nuevaTareaDescripcion = it },
                        label = { Text("Fecha Límite") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = TextFieldDefaults.outlinedTextFieldColors(
                            focusedBorderColor = Color(0xFF38C93E),
                            unfocusedBorderColor = Color(0xFFB0BEC5),
                            cursorColor = Color(0xFF38C93E),
                            focusedLabelColor = Color(0xFF38C93E),
                            unfocusedLabelColor = Color(0xFF757575)
                        )
                    )

                    Button(
                        onClick = {
                            if (nuevaTareaNombre.isNotEmpty()) {
                                val nuevaTarea = Tarea(
                                    id = 0, // ID será generado por el backend
                                    nombre = nuevaTareaNombre,
                                    descripcion = nuevaTareaDescripcion,
                                    activa = true,
                                    idUsuario = userId
                                )
                                // Crear nueva tarea en el ViewModel y limpiar los campos de entrada
                                tareaViewModel.createTarea(
                                    tarea = nuevaTarea,
                                    onSuccess = {
                                        nuevaTareaNombre = ""
                                        nuevaTareaDescripcion = ""
                                    },
                                    onError = { /* Manejo de errores si es necesario */ }
                                )
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(55.dp),
                        colors = ButtonDefaults.buttonColors(Color(0xFF38C93E))
                    ) {
                        Text(color = Color.White, text = "+ Agregar")
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Filtros para seleccionar qué tareas ver
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceAround
                ) {
                    // Opciones de filtro: "Todas", "Pendientes" y "Completadas"
                    listOf("Todas", "Pendientes", "Completadas").forEach { filtro ->
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            RadioButton(
                                selected = filtroSeleccionado == filtro,
                                onClick = { filtroSeleccionado = filtro },
                                colors = RadioButtonDefaults.colors(Color(0xFF38C93E))
                            )
                            Text(filtro)
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Lista de tareas filtradas según la selección
                tareas.filter {
                    when (filtroSeleccionado) {
                        "Pendientes" -> it.activa  // Tareas activas
                        "Completadas" -> !it.activa  // Tareas inactivas
                        else -> true  // Todas las tareas
                    }
                }.forEach { tarea ->
                    TareaItem(
                        tarea = tarea,
                        onDelete = { tareaViewModel.deleteTarea(tarea.id, {}, {}) },  // Función para eliminar la tarea
                        onComplete = {
                            tareaViewModel.updateTarea(
                                tarea.id,
                                tarea.copy(activa = false),  // Actualiza el estado a completado
                                {},
                                {}
                            )
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}


//Componente en donde se muestra cada tarea
@Composable
fun TareaItem(tarea: Tarea, onDelete: () -> Unit, onComplete: () -> Unit) {
    Card(
        shape = RoundedCornerShape(8.dp),
        backgroundColor = MaterialTheme.colors.surface,
        elevation = 2.dp,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column {
                // Muestra el nombre y descripción de la tarea, tachados si la tarea está completada
                Text(
                    text = tarea.nombre,
                    style = MaterialTheme.typography.body1.copy(
                        textDecoration = if (tarea.activa) null else TextDecoration.LineThrough
                    )
                )
                Text(
                    tarea.descripcion,
                    style = MaterialTheme.typography.body2.copy(
                        textDecoration = if (tarea.activa) null else TextDecoration.LineThrough
                    ),
                    color = Color.Gray
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(
                    text = if (tarea.activa) "Pendiente" else "Completada",
                    color = Color.White,
                    modifier = Modifier
                        .background(
                            color = if (tarea.activa) Color(0xFFEF9A9A) else Color(0xFFA5D6A7),
                            shape = RoundedCornerShape(4.dp)
                        )
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                IconButton(onClick = onComplete) {
                    Icon(Icons.Default.Check, contentDescription = "Completar", tint = Color.Gray)
                }
                IconButton(onClick = onDelete) {
                    Icon(Icons.Default.Close, contentDescription = "Eliminar", tint = Color.Red)
                }
            }
        }
    }
}




@Preview
@Composable
fun TareaPreview() {
    TareasPendientesScreen(tareaViewModel = TareaViewModel(tokenViewModel = TokenViewModel()), userId = 1)
}
