package com.example.tallericm3.navigation

// Este archivo define todas las rutas (pantallas) usadas en la navegación de la app.
// Usa una sealed class para garantizar seguridad de tipos y evitar errores con strings.

sealed class Routes(val route: String) {

    // Pantalla inicial de carga
    object Splash : Routes("splash")

    // Pantalla de inicio de sesión
    object Login : Routes("login")

    // Pantalla de registro de usuario
    object Register : Routes("register")

    // Pantalla principal con el mapa
    object Home : Routes("home")

    // Pantalla de perfil del usuario
    object Profile : Routes("profile")

    // Pantalla para editar el perfil
    object ProfileEdit : Routes("profileEdit")
}
