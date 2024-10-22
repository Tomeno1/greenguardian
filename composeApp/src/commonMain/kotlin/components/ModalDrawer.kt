package components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.Divider
import androidx.compose.material.MaterialTheme
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.automirrored.filled.Help
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.automirrored.outlined.Chat
import androidx.compose.material.icons.automirrored.outlined.Help
import androidx.compose.material.icons.automirrored.outlined.Logout
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Water
import androidx.compose.material.icons.outlined.AdminPanelSettings
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material.icons.outlined.Water
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.NavigationDrawerItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import kotlinx.coroutines.launch
import model.ItemNavigation
import viewModel.UsuarioViewModel

@Composable
fun DrawerContent(
    currentRoute: String,
    onItemClick: (String) -> Unit,
    onLogoutClick: () -> Unit,
    isAdmin: Boolean,
    usuarioViewModel: UsuarioViewModel
) {
    val userName by remember { mutableStateOf(usuarioViewModel.usuario?.nombre ?: "Invitado")  }
    val userEmail by remember { mutableStateOf(usuarioViewModel.usuario?.email ?: "Invitado")  }
    val scope = rememberCoroutineScope()
    // Registrar un lanzador de actividad para seleccionar imágenes
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            usuarioViewModel.updateUserImageUri(it) // Actualiza el estado de la imagen en el ViewModel
        }
    }

    val generalItems = listOf(
        ItemNavigation("/home", "Home", Icons.Default.Home, Icons.Outlined.Home),
    )
    val pondsItems = listOf(
        ItemNavigation("/ponds", "Estanques", Icons.Default.Water, Icons.Outlined.Water),
    )
    val adminItems = listOf(
        ItemNavigation(
            "/admin",
            "Admin",
            Icons.Default.AdminPanelSettings,
            Icons.Outlined.AdminPanelSettings
        ),
    )
    val assistantItems = listOf(
        ItemNavigation(
            "/asistente",
            "LiveChat",
            Icons.AutoMirrored.Filled.Chat,
            Icons.AutoMirrored.Outlined.Chat
        ),

        )
    val accountItems = listOf(
        ItemNavigation(
            "/ayuda", "Ayuda", Icons.AutoMirrored.Filled.Help,
            Icons.AutoMirrored.Outlined.Help
        ),
        ItemNavigation(
            "/configuracion",
            "Configuración",
            Icons.Default.Settings,
            Icons.Outlined.Settings
        ),
        ItemNavigation(
            "/logout",
            "Cerrar Sesion",
            Icons.AutoMirrored.Filled.Logout,
            Icons.AutoMirrored.Outlined.Logout
        )
    )

    Column(
        Modifier
            .fillMaxHeight(.75f)
            .verticalScroll(rememberScrollState())
            .background(Color(0xFF00001B))
    ) {
        // Banner App
        Spacer(Modifier.height(16.dp))
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(Color.Transparent)
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .background(Color.Transparent)
                    .size(80.dp) // Tamaño del círculo
                    .clickable {
                        launcher.launch("image/*") // Abre el selector de imágenes
                    }
            ) {
                // Imagen o ícono de usuario
                usuarioViewModel.userImageUri?.let {
                    Image(
                        painter = rememberAsyncImagePainter(it),
                        contentDescription = "User Image",
                        modifier = Modifier
                            .fillMaxSize()
                            .clip(CircleShape)
                            .border(width = 1.dp, color = Color.White, shape = CircleShape),
                        contentScale = ContentScale.Crop
                    )
                } ?: Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = "User Icon",
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                        .border(width = 1.dp, color = Color.White, shape = CircleShape),
                    tint = Color.White
                )

                // Ícono de la cámara superpuesto dentro del círculo
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset(x = (-1).dp, y = (-1).dp)
                        .size(24.dp)
                        .clip(CircleShape)
                        .background(Color.White)
                        .padding(2.dp)
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
            Column(modifier = Modifier.background(Color.Transparent), verticalArrangement = Arrangement.Center) {
                Text(text = userName, style = MaterialTheme.typography.h6, color = Color.White)
                Text(text = userEmail, style = MaterialTheme.typography.body2, color = Color.Gray)
            }
        }


        Spacer(Modifier.height(16.dp))

        // General Section
        Text(
            text = "General",
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.subtitle1,
            color = Color.White
        )
        generalItems.forEach { item ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute == item.route) item.outlinedIcon else item.defaultIcon,
                        contentDescription = null
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    scope.launch {
                        onItemClick(item.route)
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                shape = RoundedCornerShape(12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color(0xFF38D13F), // Color de fondo cuando está seleccionado
                    unselectedContainerColor = Color.Transparent, // Color de fondo cuando no está seleccionado
                    selectedTextColor = Color.White, // Color del texto cuando está seleccionado
                    unselectedTextColor = Color.White, // Color del texto cuando no está seleccionado
                    selectedIconColor = Color.White, // Color del ícono cuando está seleccionado
                    unselectedIconColor = Color.White
                )
            )
        }

        Divider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.White,
            thickness = 2.dp
        )

        // Ponds Section
        Text(
            text = "Ponds",
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.subtitle1,
            color = Color.White
        )
        pondsItems.forEach { item ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute == item.route) item.outlinedIcon else item.defaultIcon,
                        contentDescription = null
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    scope.launch {
                        onItemClick(item.route)
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                shape = RoundedCornerShape(12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color(0xFF38D13F), // Color de fondo cuando está seleccionado
                    unselectedContainerColor = Color.Transparent, // Color de fondo cuando no está seleccionado
                    selectedTextColor = Color.White, // Color del texto cuando está seleccionado
                    unselectedTextColor = Color.White, // Color del texto cuando no está seleccionado
                    selectedIconColor = Color.White, // Color del ícono cuando está seleccionado
                    unselectedIconColor = Color.White
                )
            )
        }

        // Admin Section
        if (isAdmin) {
            Divider(
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                color = Color.White,
                thickness = 2.dp
            )

            Text(
                text = "Admin",
                modifier = Modifier.padding(start = 16.dp),
                style = MaterialTheme.typography.subtitle1,
                color = Color.White
            )
            adminItems.forEach { item ->
                NavigationDrawerItem(
                    icon = {
                        Icon(
                            imageVector = if (currentRoute == item.route) item.outlinedIcon else item.defaultIcon,
                            contentDescription = null
                        )
                    },
                    label = { Text(item.label) },
                    selected = currentRoute == item.route,
                    onClick = {
                        scope.launch {
                            onItemClick(item.route)
                        }
                    },
                    modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                    shape = RoundedCornerShape(12.dp),
                    colors = NavigationDrawerItemDefaults.colors(
                        selectedContainerColor = Color(0xFF38D13F), // Color de fondo cuando está seleccionado
                        unselectedContainerColor = Color.Transparent, // Color de fondo cuando no está seleccionado
                        selectedTextColor = Color.White, // Color del texto cuando está seleccionado
                        unselectedTextColor = Color.White, // Color del texto cuando no está seleccionado
                        selectedIconColor = Color.White, // Color del ícono cuando está seleccionado
                        unselectedIconColor = Color.White
                    )
                )
            }
        }

        Divider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.White,
            thickness = 2.dp
        )

        // Assistant Section
        Text(
            text = "Assistant",
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.subtitle1,
            color = Color.White
        )
        assistantItems.forEach { item ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute == item.route) item.outlinedIcon else item.defaultIcon,
                        contentDescription = null
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    scope.launch {
                        onItemClick(item.route)
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                shape = RoundedCornerShape(12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color(0xFF38D13F), // Color de fondo cuando está seleccionado
                    unselectedContainerColor = Color.Transparent, // Color de fondo cuando no está seleccionado
                    selectedTextColor = Color.White, // Color del texto cuando está seleccionado
                    unselectedTextColor = Color.White, // Color del texto cuando no está seleccionado
                    selectedIconColor = Color.White, // Color del ícono cuando está seleccionado
                    unselectedIconColor = Color.White
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00001B))
    ) {
        Divider(
            modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
            color = Color.White,
            thickness = 2.dp
        )
        Text(
            text = "Cuenta",
            modifier = Modifier.padding(start = 16.dp),
            style = MaterialTheme.typography.subtitle1,
            color = Color.White
        )

        accountItems.forEach { item ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = if (currentRoute == item.route) item.outlinedIcon else item.defaultIcon,
                        contentDescription = null
                    )
                },
                label = { Text(item.label) },
                selected = currentRoute == item.route,
                onClick = {
                    scope.launch {
                        if (item.route == "/logout") {
                            onLogoutClick()
                        } else {
                            onItemClick(item.route)
                        }
                    }
                },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                shape = RoundedCornerShape(12.dp),
                colors = NavigationDrawerItemDefaults.colors(
                    selectedContainerColor = Color(0xFF38D13F), // Color de fondo cuando está seleccionado
                    unselectedContainerColor = Color.Transparent, // Color de fondo cuando no está seleccionado
                    selectedTextColor = Color.White, // Color del texto cuando está seleccionado
                    unselectedTextColor = Color.White, // Color del texto cuando no está seleccionado
                    selectedIconColor = Color.White, // Color del ícono cuando está seleccionado
                    unselectedIconColor = Color.White
                )
            )
        }


    }
}


