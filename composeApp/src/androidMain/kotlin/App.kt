import android.os.Build
import androidx.annotation.RequiresApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material.Icon
import androidx.compose.material.Scaffold
import androidx.compose.material.Text
import androidx.compose.material.TopAppBar
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.IconButton
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import components.DrawerContent
import components.SetSystemBarsColor
import data.PlantDataManager
import kotlinx.coroutines.launch
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.viewmodel.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import screen.CamaraScreen
import screen.LoginScreen
import screen.PlantDetailScreen
import screen.PlantListScreen
import screen.PondScreen
import screen.SensorScreen
import service.HttpClientProvider
import service.MqttService
import service.OpenAIService
import techminds.greenguardian.R
import viewModel.ChatViewModel
import viewModel.EstanqueViewModel
import viewModel.MqttViewModel
import viewModel.TareaViewModel
import viewModel.TokenViewModel
import viewModel.UsuarioViewModel

@RequiresApi(Build.VERSION_CODES.O)
@Composable
@Preview
fun App() {
    PreComposeApp {
        // Esta función define la estructura principal de la aplicación,
        // configurando el sistema de navegación, el Drawer (menú lateral),
        // la barra superior (TopAppBar) y las distintas pantallas (screens).
        // Se crean servicios y ViewModels necesarios para el manejo de la lógica de negocio,
        // y se establece la navegación entre pantallas, que incluyen login, home, tareas, lista de plantas,
        // detalles de plantas, estanques, sensores, cámara y asistente de chat.
        // También se configuran los colores del sistema y el comportamiento del Drawer.
        // Inicializa el sistema de navegación
        val navigator = rememberNavigator()

        // --- Dependencias y ViewModels ---

        // Configura los servicios de red y de inteligencia artificial necesarios para la aplicación
        val httpClient = HttpClientProvider.client
        val openAIService = OpenAIService(httpClient)  // Servicio para interacción con OpenAI
        val mqttService = MqttService(httpClient)      // Servicio para el manejo de MQTT

        // Crea y configura los ViewModels, que gestionan la lógica de negocio
        val chatViewModel = viewModel { ChatViewModel(openAIService) }
        val tokenViewModel = viewModel { TokenViewModel() }
        val mqttViewModel = viewModel { MqttViewModel(mqttService) }
        val estanqueViewModel = viewModel { EstanqueViewModel(tokenViewModel, mqttViewModel) }
        val userViewModel = viewModel { UsuarioViewModel(tokenViewModel) }
        val tareaViewModel = viewModel { TareaViewModel(tokenViewModel) }

        // --- Estado y configuración del Drawer ---

        // Almacena la ruta actual y el estado del menú lateral (Drawer)
        var currentRoute by remember { mutableStateOf("/login") }
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        // --- Colores del sistema ---

        // Define los colores de la barra de estado y la barra de navegación
        SetSystemBarsColor(
            statusBarColor = Color(0xFF00001B),  // Fondo oscuro para la barra de estado
            navigationBarColor = Color(0xFF00001B),  // Fondo oscuro para la barra de navegación
            useDarkNavigationIcons = true  // Íconos claros para mejor contraste
        )

        // --- Configuración del Drawer y Scaffold ---

        // Configura el menú lateral (Drawer) para la navegación
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = currentRoute != "/login" && userViewModel.usuario != null, // Habilita gestos en pantallas fuera del login
            drawerContent = {
                // Contenido del Drawer, con opciones de navegación y cierre de sesión
                DrawerContent(
                    selectedItem = currentRoute,
                    onItemSelected = { item ->  // Cambia la ruta según el ítem seleccionado
                        currentRoute = item
                        scope.launch { drawerState.close() }  // Cierra el Drawer después de seleccionar
                        navigator.navigate(item)  // Navega a la ruta seleccionada
                    },
                    onLogout = {
                        tokenViewModel.clearToken()  // Limpia el token al cerrar sesión
                        userViewModel.clearUserImage()  // Limpia la imagen del usuario actual
                        navigator.navigate("/login")  // Redirige a la pantalla de login
                        scope.launch { drawerState.close() }  // Cierra el Drawer
                    },
                    usuarioViewModel = userViewModel  // Envía el usuario actual al contenido del Drawer
                )
            },
            content = {
                // Estructura principal de la interfaz con Scaffold
                Scaffold(
                    modifier = Modifier.background(Color(0xFFEFEFEF)),  // Fondo gris claro para la interfaz
                    topBar = {
                        if (currentRoute != "/login") {  // Muestra la barra superior solo fuera del login
                            TopAppBar(
                                backgroundColor = Color(0xFF00001B),  // Fondo oscuro para la barra
                                contentColor = Color.White,  // Texto y elementos en blanco
                                title = {
                                    // Título centrado con el logo de la app y el nombre
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(verticalAlignment = Alignment.CenterVertically) {
                                            Image(
                                                painter = painterResource(id = R.drawable.greenguardian_cube),
                                                contentDescription = "Green Guardian",  // Descripción para accesibilidad
                                                modifier = Modifier.size(32.dp)  // Tamaño del ícono
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = "Green Guardian", color = Color.White)  // Texto blanco
                                        }
                                    }
                                },
                                navigationIcon = {
                                    if (currentRoute != "/home") {  // Muestra el botón de retroceso si no está en "home"
                                        IconButton(onClick = {
                                            scope.launch {
                                                navigator.popBackStack()  // Navega hacia atrás
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                                                contentDescription = "Retroceder"
                                            )
                                        }
                                    } else {
                                        IconButton(onClick = {
                                            scope.launch {
                                                drawerState.open()  // Abre el Drawer si está en "home"
                                            }
                                        }) {
                                            Icon(
                                                imageVector = Icons.Default.Menu,
                                                contentDescription = "Menu Lateral"
                                            )
                                        }
                                    }
                                },
                                actions = {
                                    // Botón de búsqueda en la barra superior
                                    IconButton(onClick = { /* Acción de búsqueda */ }) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Buscar"
                                        )
                                    }
                                    // Botón de notificaciones en la barra superior
                                    IconButton(onClick = { /* Acción de notificaciones */ }) {
                                        Icon(
                                            imageVector = Icons.Default.Notifications,
                                            contentDescription = "Notificaciones"
                                        )
                                    }
                                }
                            )
                        }
                    }
                ) { paddingValues ->
                    // --- Configuración de la navegación entre pantallas ---

                    // Define el sistema de navegación, manejando el cambio entre distintas pantallas
                    NavHost(
                        navigator,
                        "/login",
                        Modifier
                            .padding(paddingValues)
                            .background(Color(0xFFEFEFEF))
                    ) {
                        // Pantalla de login
                        scene(route = "/login") {
                            currentRoute = "/login"
                            LoginScreen(navigator, userViewModel)
                        }
                        // Pantalla de inicio
                        scene(route = "/home") {
                            currentRoute = "/home"
                            HomeScreen(navigator, userViewModel)
                        }
                        // Pantalla de tareas pendientes del usuario
                        scene(route = "/tareas") {
                            currentRoute = "/tareas"
                            val userId = userViewModel.usuario?.idUsuario
                            if (userId != null) {
                                TareasPendientesScreen(
                                    tareaViewModel = tareaViewModel,
                                    userId = userId  // Pasa el ID del usuario para filtrar sus tareas
                                )
                            }
                        }
                        // Pantalla de lista de plantas
                        scene(route = "/plantList") {
                            currentRoute = "/plantList"
                            PlantListScreen(navigator = navigator)
                        }
                        // Pantalla de detalles de una planta específica
                        scene(route = "/plantDetail/{plantName}") { backStackEntry ->
                            currentRoute = "/plantDetail"
                            val plantName = backStackEntry.path<String>("plantName")
                            val plant = PlantDataManager.getPlantByName(plantName ?: "")
                            plant?.let {
                                PlantDetailScreen(plant = it, onBack = { navigator.popBackStack() })
                            }
                        }
                        // Pantalla de estanques
                        scene(route = "/ponds") {
                            currentRoute = "/ponds"
                            PondScreen(navigator, userViewModel, estanqueViewModel)
                        }
                        // Pantalla de sensores del estanque
                        scene(route = "/sensorScreen/{estanqueId}") { backStackEntry ->
                            val estanqueId = backStackEntry.path<Long>("estanqueId")
                            estanqueId?.let {
                                SensorScreen(
                                    estanqueViewModel,
                                    mqttViewModel
                                )
                            }
                        }
                        // Pantalla de cámara
                        scene(route = "/camara") {
                            currentRoute = "/camara"
                            CamaraScreen(navigator)
                        }
                        // Pantalla del asistente de chat
                        scene(route = "/asistente") {
                            currentRoute = "/asistente"
                            ChatScreen(chatViewModel)
                        }
                    }
                }
            }
        )
    }
}
