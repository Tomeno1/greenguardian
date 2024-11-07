package components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import techminds.greenguardian.R


// --- Composable EstanqueCard ---
// Representa una tarjeta informativa sobre un estanque, incluyendo imagen, nombre y botones de acción.
@Composable
fun EstanqueCard(
    estanqueName: String,  // Nombre del estanque que se muestra en la tarjeta
    plantImage: Int,       // Imagen asociada al estanque, representada por un ID de recurso
    onClick: () -> Unit,   // Acción a realizar cuando se hace clic en la tarjeta
    buttons: @Composable () -> Unit // Composable que permite añadir botones de acción a la tarjeta
) {
    Card(
        shape = RoundedCornerShape(8.dp),  // Esquina redondeada de la tarjeta
        elevation = 4.dp,  // Sombra de elevación
        modifier = Modifier
            .padding(8.dp)  // Espacio alrededor de la tarjeta
            .fillMaxWidth()  // Ancho de la tarjeta, adaptado al contenedor
            .heightIn(min = 200.dp)  // Altura mínima de la tarjeta
            .clickable(onClick = onClick)  // Hace que la tarjeta sea clickeable
    ) {
        // Disposición vertical de los elementos dentro de la tarjeta
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,  // Alineación centrada
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)  // Relleno interno de la tarjeta
        ) {
            // Imagen del estanque en un contenedor `Box` para alineación personalizada
            Box(
                contentAlignment = Alignment.TopEnd,  // Alinea el contenido al borde superior derecho
                modifier = Modifier.fillMaxWidth()  // Ancho completo para la imagen
            ) {
                Image(
                    painter = painterResource(id = plantImage),  // Recurso de imagen pasado como parámetro
                    contentDescription = estanqueName,  // Descripción para accesibilidad
                    modifier = Modifier
                        .fillMaxWidth()  // Imagen adaptada al ancho del contenedor
                        .height(120.dp),  // Altura específica para la imagen
                    contentScale = ContentScale.Crop  // Escala la imagen para llenar el contenedor
                )
                Box(
                    modifier = Modifier
                        .size(16.dp)  // Tamaño fijo para el indicador de estado visual
                        .align(Alignment.TopEnd)  // Alinea el indicador en la esquina superior derecha
                )
            }
            Spacer(modifier = Modifier.height(8.dp))  // Espacio entre la imagen y el nombre

            // Texto que muestra el nombre del estanque
            Text(
                text = estanqueName,
                fontSize = 18.sp,  // Tamaño de fuente
                fontWeight = FontWeight.Bold,  // Negrita para resaltar el nombre
                color = Color.Black,  // Color del texto
                modifier = Modifier.align(Alignment.Start)  // Alineación a la izquierda
            )

            Spacer(modifier = Modifier.height(8.dp))  // Espacio entre el nombre y el área de botones

            // Contenedor para los botones de acción adicionales
            buttons()
        }
    }
}

