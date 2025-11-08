package com.example.tallericm3.ui.screens.profile

// Pantalla de perfil del usuario.
// Muestra la información personal del usuario actual obtenida desde Firestore.
// Permite cerrar sesión y acceder a la pantalla de edición de perfil.

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tallericm3.R
import com.example.tallericm3.navigation.Routes
import com.example.tallericm3.ui.screens.components.BottomNavBar
import com.example.tallericm3.viewModel.AuthViewModel
import com.example.tallericm3.viewModel.UserViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    navController: NavController,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel
) {
    // Se obtiene el usuario actual desde el ViewModel (flujo reactivo)
    val user by userViewModel.currentUser.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Mi Perfil", fontWeight = FontWeight.Bold) }
            )
        },
        bottomBar = {
            // Barra de navegación inferior reutilizable (con botón de logout incluido)
            BottomNavBar(
                navController = navController,
                onLogoutClick = {
                    // Al cerrar sesión se limpia el estado y se redirige al Login
                    authViewModel.logout(userViewModel) {
                        navController.navigate(Routes.Login.route) {
                            popUpTo(Routes.Home.route) { inclusive = true }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState()) // permite desplazamiento
                .padding(horizontal = 16.dp, vertical = 12.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- Imagen de perfil del usuario ---
            Box(
                contentAlignment = Alignment.Center,
                modifier = Modifier
                    .size(140.dp)
                    .border(3.dp, MaterialTheme.colorScheme.primary, CircleShape)
                    .padding(6.dp)
                    .clip(CircleShape)
                    .background(Color(0xFFF3F3F3))
            ) {
                // AsyncImage carga la imagen del usuario o un ícono por defecto
                AsyncImage(
                    model = user?.photoUrl ?: R.drawable.ici_person,
                    contentDescription = "Foto de perfil",
                    modifier = Modifier
                        .size(120.dp)
                        .clip(CircleShape),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // --- Tarjeta con los datos del usuario ---
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                ),
                elevation = CardDefaults.cardElevation(2.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(20.dp)
                ) {
                    Text(
                        "Información del usuario",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Spacer(modifier = Modifier.height(12.dp))

                    // Campos individuales con etiqueta y valor
                    ProfileItem("Nombre", user?.nombre)
                    ProfileItem("Identificación", user?.identificacion)
                    ProfileItem("Correo", user?.email)
                    ProfileItem("Teléfono", user?.telefono)
                    ProfileItem(
                        "Contraseña",
                        if (user?.password.isNullOrEmpty()) "No configurada" else "********"
                    )
                }
            }

            Spacer(modifier = Modifier.height(32.dp))

            // --- Botón para ir a la pantalla de edición ---
            Button(
                onClick = { navController.navigate(Routes.ProfileEdit.route) },
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Editar perfil")
            }

            Spacer(modifier = Modifier.height(100.dp)) // Espacio extra al final
        }
    }
}

// Componente reutilizable que muestra una fila de información del perfil
@Composable
fun ProfileItem(label: String, value: String?) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp)
    ) {
        Text(
            label,
            style = MaterialTheme.typography.bodySmall,
            color = Color.Gray
        )
        Text(
            value ?: "-",
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
    }
}
