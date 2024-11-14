package components

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.Estado


@Composable
fun EstanqueCard(
    estanqueName: String,
    plantImage: Int,
    estado: Estado, // Enum como parámetro
    onClick: () -> Unit,
    buttons: @Composable () -> Unit
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .heightIn(min = 200.dp)
            .clickable(onClick = onClick)
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            modifier = Modifier
                .fillMaxSize()
                .padding(8.dp)
        ) {
            Box(
                contentAlignment = Alignment.TopEnd,
                modifier = Modifier.fillMaxWidth()
            ) {
                Image(
                    painter = painterResource(id = plantImage),
                    contentDescription = estanqueName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = ContentScale.Crop
                )
                // Indicador de estado visual basado en el color del Enum
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(color = estado.color, shape = CircleShape)
                        .align(Alignment.TopEnd)
                )
            }
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = estanqueName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )

            Text(
                text = estado.name.replace("_", " "), // Mostramos el nombre del estado en texto
                fontSize = 14.sp,
                color = estado.color,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))

            buttons()
        }
    }
}

