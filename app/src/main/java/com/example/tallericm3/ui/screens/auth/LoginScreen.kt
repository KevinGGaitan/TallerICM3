package com.example.tallericm3.ui.screens.auth

// Este archivo define la pantalla de inicio de sesión (LoginScreen).
// Permite autenticarse con correo y contraseña o usando Google/Facebook.

import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material3.Surface
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tallericm3.ui.screens.components.*
import com.example.tallericm3.utils.SetupFacebookLogin
import com.example.tallericm3.utils.SetupGoogleLogin
import com.example.tallericm3.utils.findActivity
import com.example.tallericm3.viewModel.AuthViewModel

// Pantalla principal de inicio de sesión.
@Composable
fun LoginScreen(navController: NavHostController) {
    val context = LocalContext.current
    val activity = context.findActivity() // Contexto convertido en Activity (necesario para los intents)
    val authViewModel: AuthViewModel = viewModel() // ViewModel que maneja la lógica de autenticación

    // Campos del formulario
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    // Inicializa login con Google y Facebook
    val facebookLogin = SetupFacebookLogin(navController)
    val googleLogin = SetupGoogleLogin(navController)

    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            NormalTextComponent("Hola")
            HeadingTextComponent("Bienvenido de nuevo")

            Spacer(modifier = Modifier.height(25.dp))

            // Campo para correo electrónico
            MyTextFieldComponent(
                labelValue = "Correo electrónico",
                icon = Icons.Outlined.Email,
                value = email,
                onValueChange = { email = it }
            )

            Spacer(modifier = Modifier.height(10.dp))

            // Campo para contraseña
            PasswordTextFieldComponent(
                labelValue = "Contraseña",
                icon = Icons.Outlined.Lock,
                value = password,
                onValueChange = { password = it }
            )

            Spacer(modifier = Modifier.height(20.dp))

            // --- Botones de acción ---
            BottomComponent(
                textQuery = "¿No tienes una cuenta?",
                textClickable = "Registro",
                action = "Ingreso",
                navController = navController,

                // Acción principal: login con email y contraseña
                onPrimaryActionClick = {
                    authViewModel.login(email, password) { success ->
                        if (success) {
                            // Si el login fue exitoso, navega a Home y limpia el stack
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            // Si falla, muestra error en un Toast
                            Toast.makeText(
                                context,
                                "Error en el ingreso. Intenta de nuevo",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                },

                // Login con Google
                onGoogleClick = {
                    googleLogin(activity)
                },

                // Login con Facebook (solicita permisos básicos)
                onFacebookClick = {
                    facebookLogin(activity, listOf("email", "public_profile"))
                }
            )
        }
    }
}
