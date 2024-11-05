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
    tokenViewModel: TokenViewModel,
    usuarioViewModel: UsuarioViewModel
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF00001B))
    ) {
        Column(
            modifier = Modifier
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp),
        ) {
            Logo()
            LoginForm(navigator, usuarioViewModel)
            RememberAndForgotPass()
            Spacer(modifier = Modifier.height(60.dp))
        }
    }
}

@Composable
fun Logo() {
    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center) {
        Image(
            painter = painterResource(id = R.drawable.greenguardian),
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
    val usernameState = remember { mutableStateOf(TextFieldValue()) }
    val passwordState = remember { mutableStateOf(TextFieldValue()) }
    var errorMessage by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    Column(modifier = Modifier.fillMaxWidth()) {
        // Input de nombre de usuario
        InputField(
            label = "Nombre de Usuario",
            value = usernameState.value,
            onValueChange = { usernameState.value = it }
        )

        // Input de contraseña
        InputField(
            label = "Contraseña",
            value = passwordState.value,
            onValueChange = { passwordState.value = it },
            isPassword = true
        )

        // Botón para iniciar sesión
        CustomButton(
            text = "Iniciar Sesión",
            onClick = {
                scope.launch {
                    if (usernameState.value.text.isNotEmpty() && passwordState.value.text.isNotEmpty()) {
                        val authUsuario = AuthUsuario(
                            email = usernameState.value.text,
                            pass = passwordState.value.text
                        )
                        usuarioViewModel.loginUser(authUsuario, {
                            usuarioViewModel.usuario?.let { usuario ->
                                navigator.navigate("/home")
                            }
                        }, { error ->
                            Log.d("Login", error)
                            errorMessage = error
                        })
                    } else {
                        errorMessage = "Debe completar todos los campos"
                    }
                }
            },
            modifier = Modifier
                .padding(top = 16.dp)
                .fillMaxWidth()
                .height(50.dp)
        )

        // Mostrar mensaje de error si hay uno
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
    Text(text = label, color = Color.White)
    TextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(label) },
        colors = TextFieldDefaults.textFieldColors(
            backgroundColor = Color.White,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            focusedLabelColor = Color(0xFF38C93E),
            unfocusedLabelColor = Color(0xFF757575)
        ),
        visualTransformation = if (isPassword) PasswordVisualTransformation() else VisualTransformation.None,
        keyboardOptions = if (isPassword) KeyboardOptions(keyboardType = KeyboardType.Password) else KeyboardOptions.Default,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, Color.Gray, RoundedCornerShape(8.dp)),
        shape = RoundedCornerShape(8.dp)
    )
}

@Composable
fun RememberAndForgotPass() {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        val checkedState = remember { mutableStateOf(false) }
        Checkbox(
            checked = checkedState.value,
            onCheckedChange = { checkedState.value = it },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFF38C93E),
                uncheckedColor = Color(0xFF38C93E)
            )
        )
        Text("Recordar contraseña", color = Color.White)
        Spacer(modifier = Modifier.width(16.dp))
        Text("¿Olvidaste tu contraseña?", color = Color.Green, fontWeight = FontWeight.ExtraBold)
    }
}
