package com.example.tallericm3.ui.screens.components

// Este archivo define componentes visuales reutilizables para botones y la sección inferior de pantallas de login o registro.
// Incluye un botón con gradiente, botones sociales (Google y Facebook) y un bloque completo con separadores y navegación.

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
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.tallericm3.ui.theme.AccentColor
import com.example.tallericm3.ui.theme.GrayColor
import com.example.tallericm3.ui.theme.Secondary
import com.example.tallericm3.R


// Crea un botón ancho con fondo de gradiente horizontal entre Secondary y AccentColor.
@Composable
fun GradientButton(
    text: String,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(Color.Transparent) // Fondo transparente para que el gradiente sea visible
    ) {
        Box(
            modifier = Modifier
                .background(
                    brush = Brush.horizontalGradient(listOf(Secondary, AccentColor)), // Gradiente del botón
                    shape = RoundedCornerShape(50.dp) // Bordes redondeados tipo cápsula
                )
                .fillMaxWidth()
                .heightIn(48.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(text = text, color = Color.White, fontSize = 20.sp)
        }
    }
}


// Botón con ícono, diseñado para redes sociales (Google o Facebook).
@Composable
fun SocialButton(
    iconRes: Int,
    contentDescription: String? = null,
    onClick: () -> Unit
) {
    Button(
        onClick = onClick,
        colors = ButtonDefaults.buttonColors(Color.Transparent), // Sin color de fondo por defecto
        modifier = Modifier
            .padding(4.dp)
            .border(
                width = 2.dp,
                color = Color(android.graphics.Color.parseColor("#d2d2d2")), // Borde gris claro
                shape = RoundedCornerShape(20.dp)
            )
    ) {
        Image(
            painter = painterResource(id = iconRes),
            contentDescription = contentDescription,
            modifier = Modifier.size(30.dp) // Tamaño fijo del ícono
        )
    }
}


// Sección inferior con botón principal, divisor “Or”, botones sociales y texto para cambio de pantalla.
@Composable
fun BottomComponent(
    textQuery: String,
    textClickable: String,
    action: String,
    navController: NavHostController,
    onPrimaryActionClick: () -> Unit = {}, // Acción principal (registro o login)
    onGoogleClick: () -> Unit = {}, // Acción del botón de Google
    onFacebookClick: () -> Unit = {} // Acción del botón de Facebook
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.BottomCenter // Posiciona todo al fondo de la pantalla
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Botón principal de acción
            GradientButton(text = action, onClick = onPrimaryActionClick)

            Spacer(modifier = Modifier.height(10.dp))

            // Línea separadora con texto “Or”
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    thickness = 1.dp,
                    color = GrayColor
                )
                Text(
                    text = "Or",
                    modifier = Modifier.padding(10.dp),
                    fontSize = 20.sp,
                    color = Color.Black
                )
                Divider(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    thickness = 1.dp,
                    color = GrayColor
                )
            }

            // Fila de botones sociales (Google y Facebook)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                SocialButton(iconRes = R.drawable.google_svg, contentDescription = "Google", onClick = onGoogleClick)
                Spacer(modifier = Modifier.width(10.dp))
                SocialButton(iconRes = R.drawable.facebook_svg, contentDescription = "Facebook", onClick = onFacebookClick)
            }

            Spacer(modifier = Modifier.height(15.dp))

            // Texto inferior con navegación (ej. “¿Ya tienes cuenta? Ingreso”)
            AccountQueryComponent(textQuery, textClickable, navController)
        }
    }
}
