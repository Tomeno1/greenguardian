package components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Icon
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Eco
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Task
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.outlined.Eco
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Task
import androidx.compose.material.icons.outlined.Water
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import model.ItemNavigation
import viewModel.TokenViewModel
import viewModel.UsuarioViewModel

@Composable
fun DrawerContent(
    selectedItem: String,
    onItemSelected: (String) -> Unit,
    onLogout: () -> Unit,
    usuarioViewModel: UsuarioViewModel
) {
    // --- Selector de imagen de usuario ---
    val launcher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let { usuarioViewModel.updateUserImageUri(it) } // Actualiza la imagen del usuario en el ViewModel
    }

    // --- Imagen de usuario ---
    val userImagePainter = usuarioViewModel.userImageUri?.let { rememberAsyncImagePainter(it) }

    // --- Definición de las secciones del drawer ---
    val drawerSections = mapOf(
        "General" to listOf(
            ItemNavigation("/home", "Home", Icons.Default.Home, Icons.Outlined.Home),
            ItemNavigation("/ponds", "Estanques", Icons.Default.Water, Icons.Outlined.Water),
            ItemNavigation("/plantList", "Plantas", Icons.Default.Eco, Icons.Outlined.Eco),
            ItemNavigation("/tareas", "Tareas", Icons.Default.Task, Icons.Outlined.Task)
        ),
        "Asistencia" to listOf(
            ItemNavigation("/asistente", "Asistente", Icons.AutoMirrored.Filled.Chat, Icons.AutoMirrored.Outlined.Chat),
            ItemNavigation("/ayuda", "Ayuda", Icons.AutoMirrored.Filled.Help, Icons.AutoMirrored.Outlined.Help)
        ),
        "Configuración" to listOf(
            ItemNavigation("/configuracion", "Configuración", Icons.Default.Settings, Icons.Outlined.Settings),
            ItemNavigation("/logout", "Cerrar Sesion", Icons.AutoMirrored.Filled.Logout, Icons.AutoMirrored.Outlined.Logout)
        )
    )

    // --- Contenido del drawer ---
    ModalDrawerSheet(drawerContainerColor = Color(0xFF00001B)) {

        // --- Encabezado del drawer (imagen y datos del usuario) ---
        Row(
            modifier = Modifier.padding(16.dp).fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier.size(80.dp).clickable { launcher.launch("image/*") }
            ) {
                // Muestra la imagen del usuario o un ícono predeterminado
                if (userImagePainter != null) {
                    Image(
                        painter = userImagePainter,
                        contentDescription = "User Image",
                        modifier = Modifier.fillMaxSize().clip(CircleShape).border(1.dp, Color.White, CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "User Icon",
                        tint = Color.White,
                        modifier = Modifier.fillMaxSize().clip(CircleShape).border(1.dp, Color.White, CircleShape)
                    )
                }

                // Ícono de cámara para cambiar la imagen
                Box(
                    modifier = Modifier.align(Alignment.BottomEnd).offset((-1).dp, (-1).dp)
                        .size(24.dp).clip(CircleShape).background(Color.White).padding(2.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CameraAlt,
                        contentDescription = "Select Image",
                        tint = Color.Black,
                        modifier = Modifier.fillMaxSize()
                    )
                }
            }

            Spacer(modifier = Modifier.width(16.dp))

            // Información del usuario: nombre y correo
            Column {
                Text(
                    text = usuarioViewModel.usuario?.nombre ?: "Nombre del Usuario",
                    color = Color.White,
                    style = MaterialTheme.typography.h6
                )
                Text(
                    text = usuarioViewModel.usuario?.email ?: "correo@ejemplo.com",
                    color = Color.White.copy(alpha = 0.7f),
                    style = MaterialTheme.typography.subtitle2
                )
            }
        }

        HorizontalDivider(color = Color.White, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))

        // --- Secciones del drawer ---
        drawerSections.forEach { (sectionTitle, items) ->
            // Título de la sección
            Text(
                text = sectionTitle,
                style = MaterialTheme.typography.h6,
                color = Color.White,
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, bottom = 8.dp)
            )

            // Elementos de navegación dentro de cada sección
            items.forEach { item ->
                val isSelected = selectedItem == item.route
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = if (isSelected) item.outlinedIcon else item.defaultIcon,
                            contentDescription = null,
                            tint = Color.White
                        )
                    },
                    label = {
                        Text(
                            text = item.label,
                            color = Color.White
                        )
                    },
                    selected = isSelected,
                    onClick = {
                        if (item.route == "/logout") {
                            onLogout()
                        } else {
                            onItemSelected(item.route)
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    shape = RoundedCornerShape(12.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFF38D13F),
                        unselectedContainerColor = Color.Transparent,
                        selectedTextColor = Color.White,
                        unselectedTextColor = Color.White,
                        selectedIconColor = Color.White,
                        unselectedIconColor = Color.Gray
                    )
                )
            }

            // Divider entre secciones
            HorizontalDivider(color = Color.White, thickness = 1.dp, modifier = Modifier.padding(vertical = 8.dp))
        }
    }
}

@Preview
@Composable
fun DrawerContentPreview() {
    DrawerContent(
        selectedItem = "/home",
        onItemSelected = {},
        usuarioViewModel = UsuarioViewModel(tokenViewModel = TokenViewModel()),
        onLogout = {}
    )
}