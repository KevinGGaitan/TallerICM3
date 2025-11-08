package com.example.tallericm3.ui.screens.splash

// Pantalla de carga inicial (Splash) que determina si el usuario ya está autenticado.
// Muestra un indicador de progreso y redirige automáticamente al Home o Login
// según el estado del usuario en Firebase.

import androidx.compose.foundation.layout.*
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.tallericm3.navigation.Routes
import com.example.tallericm3.viewModel.UserViewModel
import kotlinx.coroutines.delay

@Composable
fun LoadingScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    // LaunchedEffect se ejecuta una vez cuando esta pantalla entra en composición.
    LaunchedEffect(Unit) {
        delay(1200) // Simula una breve carga antes de continuar (opcional)

        // Carga el usuario actual desde Firestore/FirebaseAuth.
        userViewModel.loadCurrentUser()

        // Si hay un usuario autenticado, va al Home; si no, al Login.
        if (userViewModel.currentUser.value != null) {
            navController.navigate(Routes.Home.route) {
                popUpTo(Routes.Splash.route) { inclusive = true } // Limpia la pila de navegación
            }
        } else {
            navController.navigate(Routes.Login.route) {
                popUpTo(Routes.Splash.route) { inclusive = true }
            }
        }
    }

    // Contenido visual del Splash: texto + indicador de carga centrado.
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Text("Cargando...", fontSize = 22.sp)
            Spacer(modifier = Modifier.height(20.dp))
            CircularProgressIndicator()
        }
    }
}
