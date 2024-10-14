package ui

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import components.CustomButton
import moe.tlaster.precompose.navigation.Navigator
import viewModel.TokenViewModel
import viewModel.UsuarioViewModel
import model.Usuario

@Composable
fun ProfileScreen(navigator: Navigator, tokenViewModel: TokenViewModel, usuarioViewModel: UsuarioViewModel) {
    val usuario by rememberUpdatedState(usuarioViewModel.usuario)

    LaunchedEffect(tokenViewModel.isLoggedIn) {
        if (!tokenViewModel.isLoggedIn) {
            navigator.navigate("/login")
        } else {
            usuarioViewModel.loadUsers()
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .background(color = Color.White),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("Perfil", style = MaterialTheme.typography.h4, fontWeight = FontWeight.Bold)

        Spacer(modifier = Modifier.height(16.dp))

        if (usuario != null) {
            UserInfo(usuario!!)
        } else {
            Text("Cargando información del usuario...", style = MaterialTheme.typography.body1)
        }

        Spacer(modifier = Modifier.height(24.dp))

        CustomButton(
            text = "Cerrar sesión",
            onClick = {
                tokenViewModel.logout(
                    onSuccess = {
                        navigator.navigate("/login")
                    },
                    onError = {
                        Log.d("ProfileScreen", "Error al cerrar sesión: $it")
                    }
                )
            }
        )

    }
}

@Composable
fun UserInfo(usuario: Usuario) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colors.primary)
        ) {
            Text(
                text = usuario.nombre.first().toString(),
                style = MaterialTheme.typography.h3,
                color = Color.White,
                modifier = Modifier.align(Alignment.Center)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = usuario.nombre,
            style = MaterialTheme.typography.h5,
            fontWeight = FontWeight.Bold
        )

        Text(
            text = usuario.role,
            style = MaterialTheme.typography.body1,
            color = MaterialTheme.colors.secondary
        )

        Spacer(modifier = Modifier.height(24.dp))

        Card(
            shape = RoundedCornerShape(8.dp),
            elevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(16.dp)
            ) {
                Text("Estanques", style = MaterialTheme.typography.h6, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                if (usuario.estanques.isNotEmpty()) {
                    usuario.estanques.forEach { estanque ->
                        Text(text = "Estanque ID: ${estanque.idEstanque}", style = MaterialTheme.typography.body1)
                    }
                } else {
                    Text("No hay estanques asignados.", style = MaterialTheme.typography.body2)
                }
            }
        }
    }
}

