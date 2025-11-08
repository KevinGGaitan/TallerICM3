package com.example.tallericm3.ui.screens.home.components

// Componente que renderiza un marcador personalizado sobre el mapa de Google Maps Compose.
// Muestra la foto del usuario o, si no tiene, su inicial dentro de un cuadro redondeado.
// Al hacer clic, el marcador se expande visualmente y puede ejecutar una acción adicional.

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.rememberAsyncImagePainter
import coil.request.ImageRequest
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.MarkerComposable

@Composable
fun CustomMapMarker(
    imageUrl: String?,
    fullName: String,
    location: LatLng,
    onClick: () -> Unit = {}
) {
    val context = LocalContext.current

    // Crea un "painter" que carga imágenes asíncronamente desde URL usando Coil.
    val painter = rememberAsyncImagePainter(
        ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(true) // animación de transición suave al cargar
            .allowHardware(false) // evita fallos en dispositivos antiguos con aceleración HW
            .build()
    )

    var expandMarker by remember { mutableStateOf(false) } // controla el tamaño animado del marcador

    // MarkerComposable: versión Compose del marcador de Google Maps.
    MarkerComposable(
        keys = arrayOf(fullName, painter.state, expandMarker), // re-renderiza si cambia algo relevante
        state = com.google.maps.android.compose.MarkerState(position = location), // posición en el mapa
        title = fullName, // nombre visible al tocar el marcador
        anchor = Offset(0.5f, 1f), // define el punto de anclaje en la base
        onClick = {
            expandMarker = !expandMarker // alterna entre pequeño y grande
            onClick()
            true
        }
    ) {
        // Contenedor visual del marcador
        Box(
            modifier = Modifier
                .size(if (expandMarker) 100.dp else 56.dp) // tamaño dinámico
                .clip(RoundedCornerShape(20.dp, 20.dp, 20.dp, 0.dp)) // forma con base plana
                .background(Color.White)
                .padding(4.dp),
            contentAlignment = Alignment.Center
        ) {
            // Si no hay foto, muestra inicial del nombre
            if (imageUrl.isNullOrEmpty()) {
                Text(
                    text = fullName.take(1).uppercase(),
                    style = MaterialTheme.typography.titleMedium,
                    color = Color.DarkGray
                )
            } else {
                // Si hay foto, la carga recortada y ajustada al marco
                Image(
                    painter = painter,
                    contentDescription = "Foto de $fullName",
                    modifier = Modifier.fillMaxSize().clip(RoundedCornerShape(16.dp)),
                    contentScale = ContentScale.Crop
                )
            }
        }
    }
}
