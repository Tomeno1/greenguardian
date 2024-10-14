package screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import components.CustomButton
import components.EstanqueCard
import components.getImageResourceByName
import model.Estanque
import model.Status
import model.Usuario

import moe.tlaster.precompose.navigation.Navigator
import viewModel.EstanqueViewModel
import viewModel.UsuarioViewModel


@Composable
fun AdminScreen(navigator: Navigator, usuarioViewModel: UsuarioViewModel) {
    val usuarios by remember { mutableStateOf(usuarioViewModel.usuarios) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    // Efecto para cargar los usuarios al iniciar la pantalla
    LaunchedEffect(Unit) {
        usuarioViewModel.loadUsers(
            onError = { error ->
                errorMessage = "Error al cargar usuarios: $error"
            }
        )
    }

    Column(
        modifier = Modifier
            .padding(16.dp)
            .fillMaxSize()
    ) {
        // Mostrar mensaje de error si existe
        errorMessage?.let {
            Text(
                text = it,
                color = Color.Red,
                style = MaterialTheme.typography.body2,
                modifier = Modifier.padding(8.dp)
            )
        }

        CustomButton(
            text = "Agregar Usuario",
            onClick = { navigator.navigate("/create_user") },
            modifier = Modifier.padding(bottom = 16.dp)
        )

        // Lista de usuarios
        LazyColumn(
            verticalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxSize()
        ) {
            items(usuarios) { usuario ->
                UsuarioItem(usuario, usuarioViewModel, navigator)
            }
        }
    }
}

@Composable
fun UsuarioItem(usuario: Usuario, usuarioViewModel: UsuarioViewModel, navigator: Navigator) {
    Card(
        backgroundColor = Color.White,
        elevation = 8.dp,
        modifier = Modifier
            .fillMaxWidth()
            .clickable { navigator.navigate("/edit_user/${usuario.idUsuario}") }
            .padding(8.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Nombre: ${usuario.nombre}", style = MaterialTheme.typography.body1)
            Text(text = "Rol: ${usuario.role}", style = MaterialTheme.typography.body1)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                CustomButton(
                    text = "Estanques",
                    onClick = { navigator.navigate("/list_estanques/${usuario.idUsuario}") }
                )
                Spacer(modifier = Modifier.width(8.dp))
                CustomButton(
                    text = "Eliminar",
                    onClick = {
                        usuarioViewModel.deleteUser(
                            userId = usuario.idUsuario,
                            onSuccess = {
                                Log.d("AdminScreen", "Usuario eliminado")
                            },
                            onError = { errorMessage ->
                                Log.d("AdminScreen", "Error al eliminar usuario: $errorMessage")
                            }
                        )
                    }
                )
            }
        }
    }
}


@Composable
fun EditUserScreen(navigator: Navigator, usuarioViewModel: UsuarioViewModel, userId: String) {
    val usuario = usuarioViewModel.usuarios.find { it.idUsuario.toString() == userId }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    if (usuario != null) {
        val nombre = remember { mutableStateOf(TextFieldValue(usuario.nombre)) }
        val role = remember { mutableStateOf(TextFieldValue(usuario.role)) }

        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = "Editar Usuario: ${usuario.idUsuario}", style = MaterialTheme.typography.h6)

            TextField(
                value = nombre.value,
                onValueChange = { nombre.value = it },
                label = { Text("Nombre") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            TextField(
                value = role.value,
                onValueChange = { role.value = it },
                label = { Text("Rol") },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )

            errorMessage?.let {
                Text(text = it, color = Color.Red, style = MaterialTheme.typography.body2)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                CustomButton(
                    text = "Guardar",
                    onClick = {
                        if (nombre.value.text.isEmpty() || role.value.text.isEmpty()) {
                            errorMessage = "Todos los campos son obligatorios."
                        } else {
                            val updatedUser = usuario.copy(
                                nombre = nombre.value.text,
                                role = role.value.text
                            )
                            usuarioViewModel.editUser(
                                updatedUser,
                                onSuccess = {
                                    navigator.goBack()
                                },
                                onError = { errorMessage ->
                                    Log.d(
                                        "EditUserScreen",
                                        "Error al editar usuario: $errorMessage"
                                    )
                                }
                            )
                        }
                    }
                )
            }
        }
    } else {
        Text("Usuario no encontrado", style = MaterialTheme.typography.h6)
    }
}


@Composable
fun CreateUserScreen(navigator: Navigator, usuarioViewModel: UsuarioViewModel) {
    val nombre = remember { mutableStateOf(TextFieldValue("")) }
    val role = remember { mutableStateOf(TextFieldValue("")) }
    val password = remember { mutableStateOf(TextFieldValue("")) }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    errorMessage.value?.let {
        LaunchedEffect(key1 = it) {
            kotlinx.coroutines.delay(5000)
            errorMessage.value = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDEDED)),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Crear Usuario", style = MaterialTheme.typography.h6)
            TextField(
                value = nombre.value,
                onValueChange = { nombre.value = it },
                label = { Text("Nombre") },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            TextField(
                value = role.value,
                onValueChange = { role.value = it },
                label = { Text("Role") },
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            TextField(
                value = password.value,
                onValueChange = { password.value = it },
                label = { Text("Contraseña") },
                visualTransformation = PasswordVisualTransformation(),
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
                colors = TextFieldDefaults.textFieldColors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
            )
            errorMessage.value?.let {
                Text(text = it, color = Color.Red, style = MaterialTheme.typography.body2)
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                CustomButton(
                    text = "Crear",
                    onClick = {
                        if (nombre.value.text.isEmpty() || role.value.text.isEmpty() || password.value.text.isEmpty()) {
                            errorMessage.value = "Todos los campos son obligatorios."
                        } else {
                            errorMessage.value = null
                            val newUser = Usuario(
                                idUsuario = 0, // La ID será asignada por el servidor
                                nombre = nombre.value.text,
                                role = role.value.text,
                                contraseña = password.value.text,
                                estanques = listOf()
                            )
                            usuarioViewModel.createUser(newUser,
                                onSuccess = {
                                    navigator.goBack()
                                },
                                onError = { errorMessage ->
                                    Log.d(
                                        "CreateUserScreen",
                                        "Error al crear usuario: $errorMessage"
                                    )
                                }
                            )
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun CreateEstanqueScreen(
    navigator: Navigator,
    estanqueViewModel: EstanqueViewModel,
    userId: String
) {
    val nombre = remember { mutableStateOf(TextFieldValue("")) }
    val status = remember { mutableStateOf(Status.GOOD) }
    val imageName = remember { mutableStateOf(TextFieldValue("")) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val expanded = remember { mutableStateOf(false) }

    errorMessage.value?.let {
        LaunchedEffect(key1 = it) {
            kotlinx.coroutines.delay(5000)
            errorMessage.value = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDEDED)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            backgroundColor = Color.White,
            elevation = 8.dp,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .defaultMinSize(minHeight = 400.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Crear Estanque", style = MaterialTheme.typography.h6)
                TextField(
                    value = nombre.value,
                    onValueChange = { nombre.value = it },
                    label = { Text("Nombre") },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color.White)
                        .clickable { expanded.value = true }
                        .padding(16.dp)
                ) {
                    Text(
                        text = "Estado: ${status.value.name}",
                        style = MaterialTheme.typography.body1
                    )
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Status.entries.forEach { statusValue ->
                            DropdownMenuItem(onClick = {
                                status.value = statusValue
                                expanded.value = false
                            }) {
                                Text(text = statusValue.name)
                            }
                        }
                    }
                }

                TextField(
                    value = imageName.value,
                    onValueChange = { imageName.value = it },
                    label = { Text("Nombre de la Imagen") },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                errorMessage.value?.let {
                    Text(text = it, color = Color.Red, style = MaterialTheme.typography.body2)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    CustomButton(
                        text = "Crear",
                        onClick = {
                            if (nombre.value.text.isEmpty() || imageName.value.text.isEmpty()) {
                                errorMessage.value = "Todos los campos son obligatorios."
                            } else {
                                errorMessage.value = null
                                val newEstanque = Estanque(
                                    idEstanque = 0, // La ID será asignada por el servidor
                                    nombre = nombre.value.text,
                                    status = status.value,
                                    image_name = imageName.value.text
                                )
                                estanqueViewModel.createEstanque(newEstanque, userId.toLong(),
                                    onSuccess = {
                                        navigator.goBack()
                                    },
                                    onError = { errorMessage ->
                                        Log.d(
                                            "CreateEstanqueScreen",
                                            "Error al crear estanque: $errorMessage"
                                        )
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EstanqueItem(estanque: Estanque, onEditClick: () -> Unit, onDeleteClick: () -> Unit) {
    EstanqueCard(
        estanqueName = estanque.nombre,
        plantImage = getImageResourceByName(estanque.image_name),
        status = estanque.status,
        onClick = {},
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.End
        ) {
            CustomButton(
                text = "Editar",
                onClick = onEditClick,
                modifier = Modifier.padding(end = 8.dp)
            )
            CustomButton(
                text = "Eliminar",
                onClick = onDeleteClick
            )
        }
    }
}


@Composable
fun ListEstanquesScreen(
    navigator: Navigator,
    estanqueViewModel: EstanqueViewModel,
    userId: String
) {
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val showAlert = remember { mutableStateOf(false) }

    LaunchedEffect(userId) {
        estanqueViewModel.loadEstanques(userId.toLong(),
            onSuccess = {
                if (it.isEmpty()) {
                    showAlert.value = true
                }
            },
            onError = {
                showAlert.value = true
            }
        )
    }

    val estanques = estanqueViewModel.estanques

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDEDED)),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "Estanques del Usuario", style = MaterialTheme.typography.h6)
            errorMessage.value?.let {
                Text(text = it, color = Color.Red, style = MaterialTheme.typography.body2)
            }
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.weight(1f)
            ) {
                items(estanques) { estanque ->
                    EstanqueItem(
                        estanque = estanque,
                        onEditClick = { navigator.navigate("/edit_estanque/${estanque.idEstanque}") },
                        onDeleteClick = {
                            estanqueViewModel.deleteEstanque(estanque.idEstanque,
                                onSuccess = {
                                    // Actualiza la lista de estanques
                                    estanqueViewModel.loadEstanques(
                                        userId.toLong(),
                                        onSuccess = {},
                                        onError = {})
                                },
                                onError = { errorMessage ->
                                    Log.d(
                                        "ListEstanquesScreen",
                                        "Error al eliminar estanque: $errorMessage"
                                    )
                                }
                            )
                        }
                    )
                }
            }
            CustomButton(
                text = "Agregar Estanque",
                onClick = { navigator.navigate("/create_estanque/$userId") },
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (showAlert.value) {
            AlertDialog(
                onDismissRequest = { showAlert.value = false },
                title = { Text("Sin Estanques") },
                text = { Text("El usuario no tiene estanques registrados.") },
                confirmButton = {
                    CustomButton(
                        text = "Aceptar",
                        onClick = { showAlert.value = false }
                    )
                }
            )
        }
    }
}

@Composable
fun EditEstanqueScreen(
    navigator: Navigator,
    estanqueViewModel: EstanqueViewModel,
    estanqueId: String
) {
    val estanque = estanqueViewModel.estanques.find { it.idEstanque.toString() == estanqueId }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    if (estanque != null) {
        val nombre = remember { mutableStateOf(TextFieldValue(estanque.nombre)) }
        val status = remember { mutableStateOf(estanque.status) }
        val imageName = remember { mutableStateOf(TextFieldValue(estanque.image_name)) }
        val expanded = remember { mutableStateOf(false) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEDEDED)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                backgroundColor = Color.White,
                elevation = 8.dp,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 400.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Editar Estanque", style = MaterialTheme.typography.h6)
                    TextField(
                        value = nombre.value,
                        onValueChange = { nombre.value = it },
                        label = { Text("Nombre") },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .background(Color.White)
                            .clickable { expanded.value = true }
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Estado: ${status.value.name}",
                            style = MaterialTheme.typography.body1
                        )
                        DropdownMenu(
                            expanded = expanded.value,
                            onDismissRequest = { expanded.value = false },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Status.entries.forEach { statusValue ->
                                DropdownMenuItem(onClick = {
                                    status.value = statusValue
                                    expanded.value = false
                                }) {
                                    Text(text = statusValue.name)
                                }
                            }
                        }
                    }

                    TextField(
                        value = imageName.value,
                        onValueChange = { imageName.value = it },
                        label = { Text("Nombre de la Imagen") },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                    errorMessage.value?.let {
                        Text(text = it, color = Color.Red, style = MaterialTheme.typography.body2)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        CustomButton(
                            text = "Guardar",
                            onClick = {
                                if (nombre.value.text.isEmpty() || imageName.value.text.isEmpty()) {
                                    errorMessage.value = "Todos los campos son obligatorios."
                                } else {
                                    errorMessage.value = null
                                    val updatedEstanque = Estanque(
                                        idEstanque = estanque.idEstanque,
                                        nombre = nombre.value.text,
                                        status = status.value,
                                        image_name = imageName.value.text,
                                        usuario = estanque.usuario
                                    )
                                    estanqueViewModel.updateEstanque(updatedEstanque,
                                        onSuccess = {
                                            navigator.goBack()
                                        },
                                        onError = { errorMessage ->
                                            Log.d(
                                                "EditEstanqueScreen",
                                                "Error al editar estanque: $errorMessage"
                                            )
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    } else {
        Text("Estanque no encontrado", style = MaterialTheme.typography.h6)
    }
}

/* @Composable
fun CreateCultivoScreen(navigator: Navigator, sessionViewModel: SessionViewModel) {
    val nombre = remember { mutableStateOf(TextFieldValue("")) }
    val imageName = remember { mutableStateOf(TextFieldValue("")) }
    val errorMessage = remember { mutableStateOf<String?>(null) }
    val expanded = remember { mutableStateOf(false) }

    errorMessage.value?.let {
        LaunchedEffect(key1 = it) {
            kotlinx.coroutines.delay(5000)
            errorMessage.value = null
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFEDEDED)),
        contentAlignment = Alignment.Center
    ) {
        Card(
            backgroundColor = Color.White,
            elevation = 8.dp,
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth()
                .defaultMinSize(minHeight = 400.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "Crear Cultivo", style = MaterialTheme.typography.h6)
                TextField(
                    value = nombre.value,
                    onValueChange = { nombre.value = it },
                    label = { Text("Nombre") },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )

                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                        .background(Color.White)
                        .clickable { expanded.value = true }
                        .padding(16.dp)
                ) {
                    DropdownMenu(
                        expanded = expanded.value,
                        onDismissRequest = { expanded.value = false },
                        modifier = Modifier.fillMaxWidth()
                    ) {

                    }
                }

                TextField(
                    value = imageName.value,
                    onValueChange = { imageName.value = it },
                    label = { Text("Nombre de la Imagen") },
                    colors = TextFieldDefaults.textFieldColors(
                        focusedIndicatorColor = Color.Transparent,
                        unfocusedIndicatorColor = Color.Transparent
                    ),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 4.dp)
                )
                errorMessage.value?.let {
                    Text(text = it, color = Color.Red, style = MaterialTheme.typography.body2)
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.End
                ) {
                    CustomButton(
                        text = "Crear",
                        onClick = {
                            if (nombre.value.text.isEmpty() || imageName.value.text.isEmpty()) {
                                errorMessage.value = "Todos los campos son obligatorios."
                            } else {
                                errorMessage.value = null
                                val newCultivo = Cultivo(
                                    idCultivo = 0, // La ID será asignada por el servidor
                                    nombre = nombre.value.text,
                                    image_name = imageName.value.text
                                )
                                sessionViewModel.createCultivo(newCultivo,
                                    onSuccess = {
                                        navigator.goBack()
                                    },
                                    onError = { errorMessage ->
                                        Log.d(
                                            "CreateCultivoScreen",
                                            "Error al crear estanque: $errorMessage"
                                        )
                                    }
                                )
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun EditCultivoScreen(navigator: Navigator, sessionViewModel: SessionViewModel, cultivoId: String) {
    val cultivo = sessionViewModel.cultivos.find { it.idCultivo.toString() == cultivoId }
    val errorMessage = remember { mutableStateOf<String?>(null) }

    if (cultivo != null) {
        val nombre = remember { mutableStateOf(TextFieldValue(cultivo.nombre)) }
        val imageName = remember { mutableStateOf(TextFieldValue(cultivo.image_name)) }

        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFEDEDED)),
            contentAlignment = Alignment.Center
        ) {
            Card(
                backgroundColor = Color.White,
                elevation = 8.dp,
                modifier = Modifier
                    .padding(16.dp)
                    .fillMaxWidth()
                    .defaultMinSize(minHeight = 400.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "Editar Cultivo", style = MaterialTheme.typography.h6)
                    TextField(
                        value = nombre.value,
                        onValueChange = { nombre.value = it },
                        label = { Text("Nombre") },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )

                    TextField(
                        value = imageName.value,
                        onValueChange = { imageName.value = it },
                        label = { Text("Nombre de la Imagen") },
                        colors = TextFieldDefaults.textFieldColors(
                            focusedIndicatorColor = Color.Transparent,
                            unfocusedIndicatorColor = Color.Transparent
                        ),
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                    )
                    errorMessage.value?.let {
                        Text(text = it, color = Color.Red, style = MaterialTheme.typography.body2)
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        CustomButton(
                            text = "Guardar",
                            onClick = {
                                if (nombre.value.text.isEmpty() || imageName.value.text.isEmpty()) {
                                    errorMessage.value = "Todos los campos son obligatorios."
                                } else {
                                    errorMessage.value = null
                                    val updatedCultivo = Cultivo(
                                        idCultivo = cultivo.idCultivo,
                                        nombre = nombre.value.text,
                                        image_name = imageName.value.text
                                    )
                                    sessionViewModel.updateCultivos(updatedCultivo,
                                        onSuccess = {
                                            navigator.goBack()
                                        },
                                        onError = { errorMessage ->
                                            Log.d("EditCultivoScreen", "Error al editar cultivo: $errorMessage")
                                        }
                                    )
                                }
                            }
                        )
                    }
                }
            }
        }
    } else {
        Text("Cultivo no encontrado", style = MaterialTheme.typography.h6)
    }
} */








