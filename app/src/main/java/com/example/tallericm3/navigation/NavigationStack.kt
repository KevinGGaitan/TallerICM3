package com.example.tallericm3.navigation

// Este archivo define el sistema de navegación principal de la app (NavigationStack).
// Controla las pantallas disponibles y sus rutas dentro del NavHost de Jetpack Compose.

import android.app.Application
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.tallericm3.data.repository.RealtimeRepository
import com.example.tallericm3.ui.screens.auth.LoginScreen
import com.example.tallericm3.ui.screens.auth.SignupScreen
import com.example.tallericm3.ui.screens.home.HomeScreen
import com.example.tallericm3.ui.screens.profile.ProfileEditScreen
import com.example.tallericm3.ui.screens.profile.ProfileScreen
import com.example.tallericm3.ui.screens.splash.LoadingScreen
import com.example.tallericm3.viewModel.*

// Función que define todas las rutas y ViewModels que componen la navegación de la app.
@Composable
fun NavigationStack(
    userViewModel: UserViewModel = viewModel(),
    authViewModel: AuthViewModel = viewModel()
) {
    val app = LocalContext.current.applicationContext as Application // Contexto global de la app
    val navController = rememberNavController() // Controlador de navegación

    // Se crea el MapViewModel con su fábrica personalizada (necesita Application y dependencias)
    val mapViewModel: MapViewModel = viewModel(
        factory = MapViewModelFactory(
            app = app,
            realtimeRepository = RealtimeRepository(),
            userViewModel = userViewModel
        )
    )

    // Definición del gráfico de navegación
    NavHost(
        navController = navController,
        startDestination = Routes.Splash.route // Pantalla inicial
    ) {
        // Pantalla de carga (decide si va a Login o Home)
        composable(Routes.Splash.route) {
            LoadingScreen(navController, userViewModel)
        }

        // Pantalla de inicio de sesión
        composable(Routes.Login.route) {
            LoginScreen(navController)
        }

        // Pantalla de registro
        composable(Routes.Register.route) {
            SignupScreen(navController)
        }

        // Pantalla principal con el mapa
        composable(Routes.Home.route) {
            HomeScreen(
                mapViewModel = mapViewModel,
                userViewModel = userViewModel,
                navController = navController,
                authViewModel = authViewModel
            )
        }

        // Pantalla de perfil del usuario
        composable(Routes.Profile.route) {
            ProfileScreen(
                navController = navController,
                userViewModel = userViewModel,
                authViewModel = authViewModel
            )
        }

        // Pantalla de edición de perfil
        composable(Routes.ProfileEdit.route) {
            ProfileEditScreen(navController, userViewModel)
        }
    }
}
