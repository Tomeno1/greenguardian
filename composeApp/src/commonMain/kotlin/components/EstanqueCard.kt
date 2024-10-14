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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import model.Status

val DarkGreen = Color(0xFF006400) // Verde oscuro
val DarkYellow = Color(0xFFCCCC00) // Amarillo oscuro
val DarkRed = Color(0xFF8B0000) // Rojo oscuro


@Composable
fun EstanqueCard(
    estanqueName: String,
    plantImage: Int,
    status: Status,
    onClick: () -> Unit,
    buttons: @Composable () -> Unit
) {
    val statusColor = when (status) {
        Status.GOOD -> DarkGreen
        Status.PRECAUTION -> DarkYellow
        Status.WARNING -> DarkRed
    }

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = 4.dp,
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
            .heightIn(min = 200.dp)
            .clickable(onClick = onClick)  // Hacemos la tarjeta clickeable
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
                Box(
                    modifier = Modifier
                        .size(16.dp)
                        .background(statusColor, shape = CircleShape)
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
                text = "Estado: ${status.name}",
                fontSize = 14.sp,
                color = statusColor,
                modifier = Modifier.align(Alignment.Start)
            )
            Spacer(modifier = Modifier.height(8.dp))
            buttons()
        }
    }
}

@Composable
fun CultivoCard(
    cultivoName: String,
    plantImage: Int,
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
            .clickable(onClick = onClick)  // Hacemos la tarjeta clickeable
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
                    contentDescription = cultivoName,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(120.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = cultivoName,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                color = Color.Black,
                modifier = Modifier.align(Alignment.Start)
            )

            Spacer(modifier = Modifier.height(8.dp))
            buttons()
        }
    }
}

@Composable
fun StatisticCard(title: String, value: String, image: Int) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(4.dp)
    ) {
        Text(text = "Estanques", style = MaterialTheme.typography.h6.copy(fontWeight = FontWeight.Bold))
        Spacer(modifier = Modifier.height(8.dp))
        Card(
            backgroundColor = Color.White,
            elevation = 8.dp,
            modifier = Modifier
                .width(120.dp)
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Image(
                    painter = painterResource(id = image),
                    contentDescription = title,
                    modifier = Modifier.size(40.dp) // Ajusta el tamaño según sea necesario
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = value, style = MaterialTheme.typography.h6)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(text = title, style = MaterialTheme.typography.body1.copy( fontWeight = FontWeight.Bold))
    }
}


