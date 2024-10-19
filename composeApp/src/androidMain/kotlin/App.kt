/*import screen.AdminScreen
import screen.CreateEstanqueScreen
import screen.CreateUserScreen
import screen.EditEstanqueScreen
import screen.EditUserScreen
import screen.ListEstanquesScreen*/
//import viewModel.EstanqueViewModel
import android.util.Log
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
import androidx.compose.material3.ModalDrawerSheet
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
import data.HttpClientProvider
import data.OpenAIService
import kotlinx.coroutines.launch
import moe.tlaster.precompose.PreComposeApp
import moe.tlaster.precompose.navigation.NavHost
import moe.tlaster.precompose.navigation.path
import moe.tlaster.precompose.navigation.rememberNavigator
import moe.tlaster.precompose.viewmodel.viewModel
import org.jetbrains.compose.ui.tooling.preview.Preview
import screen.CamaraScreen
import screen.LoginScreen
import screen.PondScreen
import screen.SensorScreen
import techminds.greenguardian.R
import viewModel.EstanqueViewModel
import viewModel.TokenViewModel
import viewModel.UsuarioViewModel

@Composable
@Preview
fun App() {
    PreComposeApp {
        val navigator = rememberNavigator()
        val openAIService = OpenAIService(HttpClientProvider.client)
        val chatViewModel = viewModel { ChatViewModel(openAIService) }
        val estanqueViewModel = viewModel { EstanqueViewModel(TokenViewModel()) }
        val tokenViewModel = viewModel { TokenViewModel() }
        val userViewModel =
            viewModel(keys = listOf(tokenViewModel)) { UsuarioViewModel(tokenViewModel) }
        var currentRoute by remember { mutableStateOf("/login") }
        val drawerState = rememberDrawerState(DrawerValue.Closed)
        val scope = rememberCoroutineScope()

        SetSystemBarsColor(
            statusBarColor = Color(0xFF00001B),
            navigationBarColor = Color(0xFF00001B),
            useDarkNavigationIcons = true
        )

        // Modal Navigation Drawer with DrawerContent
        ModalNavigationDrawer(
            drawerState = drawerState,
            gesturesEnabled = currentRoute != "/login" && userViewModel.usuario != null,
            drawerContent = {
                if (userViewModel.usuario != null) {
                    ModalDrawerSheet {
                        DrawerContent(
                            currentRoute = currentRoute,
                            onItemClick = { route ->
                                scope.launch {
                                    drawerState.close()  // Cierra el drawer al seleccionar una ruta
                                    currentRoute = route
                                    navigator.navigate(route)
                                }
                            },
                            userName = userViewModel.usuario?.nombre
                                ?: "Guest",  // Pasa el nombre del usuario
                            onLogoutClick = {
                                tokenViewModel.logout(
                                    onSuccess = {
                                        scope.launch {
                                            drawerState.close()
                                            currentRoute = "/login"
                                            navigator.navigate("/login")
                                        }
                                    },
                                    onError = { errorMessage ->
                                        Log.d("App", "Error al cerrar sesion: $errorMessage")
                                    }
                                )
                            },
                            isAdmin = userViewModel.usuario?.role == "ADMIN",
                            usuarioViewModel = userViewModel
                        )
                    }
                }
            },
            content = {
                Scaffold(
                    modifier = Modifier.background(Color(0xFFEFEFEF)), // Color de fondo por defecto
                    topBar = {
                        if (currentRoute != "/login") {
                            TopAppBar(
                                backgroundColor = Color(0xFF00001B),
                                contentColor = Color.White,
                                title = {
                                    Box(
                                        modifier = Modifier.fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Image(
                                                painter = painterResource(id = R.drawable.greenguardian_cube),
                                                contentDescription = "Green Guardian",
                                                modifier = Modifier.size(32.dp)
                                            )
                                            Spacer(modifier = Modifier.width(8.dp))
                                            Text(text = "Green Guardian", color = Color.White)
                                        }
                                    }
                                },
                                navigationIcon = {
                                    if (currentRoute != "/home") {
                                        IconButton(onClick = {
                                            scope.launch {
                                                navigator.popBackStack()
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
                                                drawerState.open()
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
                                    IconButton(onClick = { /* Acción para buscar */ }) {
                                        Icon(
                                            imageVector = Icons.Default.Search,
                                            contentDescription = "Buscar"
                                        )
                                    }

                                    IconButton(onClick = { /* Acción para notificaciones */ }) {
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
                    NavHost(
                        navigator,
                        "/login",
                        Modifier
                            .padding(paddingValues)
                            .background(Color(0xFFEFEFEF))
                    ) {
                        scene(route = "/login") {
                            currentRoute = "/login"
                            LoginScreen(navigator, tokenViewModel, userViewModel)
                        }
                        scene(route = "/home") {
                            currentRoute = "/home"
                            HomeScreen(navigator, userViewModel)
                        }
                       scene(route = "/ponds") {
                            currentRoute = "/ponds"
                            PondScreen(navigator, userViewModel, estanqueViewModel)
                        }
                        scene(route = "/sensorScreen/{estanqueId}") { backStackEntry ->
                            val estanqueId = backStackEntry.path<Long>("estanqueId")
                            estanqueId?.let {
                                SensorScreen(estanqueViewModel)  // Mostrar la pantalla de sensores
                            }
                        }
                        scene(route = "/camara") {
                            currentRoute = "/camara"
                            CamaraScreen(navigator)
                        }
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
