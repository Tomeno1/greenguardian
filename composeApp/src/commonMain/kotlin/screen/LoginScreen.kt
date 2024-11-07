package screen

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
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
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.Checkbox
import androidx.compose.material.CheckboxDefaults
import androidx.compose.material.Text
import androidx.compose.material.TextField
import androidx.compose.material.TextFieldDefaults
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
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import components.CustomButton
import kotlinx.coroutines.launch
import model.AuthUsuario
import model.Usuario
import moe.tlaster.precompose.navigation.Navigator
import techminds.greenguardian.R
import viewModel.TokenViewModel
import viewModel.UsuarioViewModel

@Composable
fun LoginScreen(
    navigator: Navigator,

    usuarioViewModel: UsuarioViewModel
) {
    // Pantalla de inicio de sesión, fondo oscuro y columnas de contenido
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00001B))  // Fondo oscuro
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Logo()                         // Logo de la aplicación
            LoginForm(navigator, usuarioViewModel)   // Formulario de entrada de usuario
            RememberAndForgotPass()         // Opciones de recordar contraseña y recuperación
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun Logo() {
    // Muestra el logo de la aplicación
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.greenguardian),  // Imagen del logo
            contentDescription = "Logo",
            modifier = Modifier.size(300.dp)
        )
    }
}

@Composable
fun LoginForm(
    navigator: Navigator,
    usuarioViewModel: UsuarioViewModel
) {
    val usernameState = remember { mutableStateOf(TextFieldValue()) }  // Estado del campo de usuario
    val passwordState = remember { mutableStateOf(TextFieldValue()) }  // Estado del campo de contraseña
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Campo de entrada de nombre de usuario
        InputField(
            label = "Nombre de Usuario",
            value = usernameState.value,
            onValueChange = { usernameState.value = it }
        )

        // Campo de entrada de contraseña
        InputField(
            label = "Contraseña",
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            isPassword = true
        )

        // Botón de inicio de sesión
        CustomButton(
            text = "Iniciar Sesión",
            onClick = {
                scope.launch {
                    // Verifica que ambos campos estén completos
                    if (usernameState.value.text.isNotEmpty() && passwordState.value.text.isNotEmpty()) {
                        val authUsuario = AuthUsuario(
                            email = usernameState.value.text,
                            pass = passwordState.value.text
                        )
                        // Inicia sesión usando el ViewModel
                        usuarioViewModel.loginUser(authUsuario, {
                            usuarioViewModel.usuario?.let { usuario ->
                                navigator.navigate("/home")  // Navega a la pantalla de inicio
                            }
                        }, { error ->
                            Log.d("Login", error)
                            errorMessage = error  // Muestra mensaje de error si la autenticación falla
                        })
                    } else {
                        errorMessage = "Debe completar todos los campos"  // Mensaje de error si faltan campos
                    }
                }
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(50.dp)
        )

        // Muestra el mensaje de error en rojo si existe
        if (errorMessage.isNotEmpty()) {
            Text(text = errorMessage, color = Color.Red, modifier = Modifier.padding(top = 16.dp))
        }
    }
}

@Composable
fun InputField(
    label: String,
    value: TextFieldValue,
    onValueChange: (TextFieldValue) -> Unit,
    isPassword: Boolean = false
) {
    // Campo de entrada con opción para visualización de contraseña
    Text(text = label, color = Color.White)
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedLabelColor = Color(0xFF38C93E),   // Color al enfocarse
            unfocusedLabelColor = Color(0xFF757575)   // Color cuando no está enfocado
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),  // Borde de entrada de texto
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun RememberAndForgotPass() {
    // Opciones de "Recordar contraseña" y "¿Olvidaste tu contraseña?"
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val checkedState = remember { mutableStateOf(false) }  // Estado del checkbox
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = { checkedState.value = it },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF38C93E),  // Color cuando está seleccionado
                uncheckedColor = Color(0xFF38C93E) // Color cuando no está seleccionado
            )
        )
        Text("Recordar contraseña", color = Color.White)  // Texto "Recordar contraseña"
        Spacer(modifier = Modifier.width(16.dp))
        Text("¿Olvidaste tu contraseña?", color = Color.Green, fontWeight = FontWeight.ExtraBold)  // Texto para recuperar contraseña
    }
}