package com.example.tallericm3.ui.screens.auth

// Este archivo define la pantalla de registro (SignupScreen), donde el usuario puede crear una nueva cuenta
// mediante formulario o autenticarse con Google o Facebook.

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.outlined.Call
import androidx.compose.material.icons.outlined.Email
import androidx.compose.material.icons.outlined.Lock
import androidx.compose.material.icons.outlined.Person
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
import com.example.tallericm3.utils.findActivity
import com.example.tallericm3.viewModel.AuthViewModel
import android.widget.Toast
import com.example.tallericm3.utils.SetupGoogleLogin

// Pantalla de registro: permite crear cuenta, validar campos y conectarse con redes sociales.
@Composable
fun SignupScreen(navController: NavHostController) {

    val context = LocalContext.current
    val activity = context.findActivity() // Obtiene la Activity actual, necesaria para iniciar actividades externas

    val authViewModel: AuthViewModel = viewModel() // ViewModel que maneja la lógica de registro

    // Estados para los campos del formulario
    var nombre by remember { mutableStateOf("") }
    var identificacion by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var telefono by remember { mutableStateOf("") }

    // Inicializa funciones de login social
    val facebookLogin = SetupFacebookLogin(navController)
    val googleLogin = SetupGoogleLogin(navController)

    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(28.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState()) // Permite desplazarse si el teclado cubre parte del formulario
        ) {
            NormalTextComponent(value = "Hola de nuevo")
            HeadingTextComponent(value = "Crea una cuenta")
            Spacer(modifier = Modifier.height(25.dp))

            // --- Campos del formulario ---
            MyTextFieldComponent(
                labelValue = "Nombre",
                icon = Icons.Outlined.Person,
                value = nombre,
                onValueChange = { nombre = it }
            )
            Spacer(modifier = Modifier.height(10.dp))

            MyTextFieldComponent(
                labelValue = "Número de identidad",
                icon = Icons.Outlined.Person,
                value = identificacion,
                onValueChange = { identificacion = it }
            )
            Spacer(modifier = Modifier.height(10.dp))

            MyTextFieldComponent(
                labelValue = "Correo electrónico",
                icon = Icons.Outlined.Email,
                value = email,
                onValueChange = { email = it }
            )
            Spacer(modifier = Modifier.height(10.dp))

            PasswordTextFieldComponent(
                labelValue = "Contraseña",
                icon = Icons.Outlined.Lock,
                value = password,
                onValueChange = { password = it }
            )
            Spacer(modifier = Modifier.height(10.dp))

            MyTextFieldComponent(
                labelValue = "Teléfono",
                icon = Icons.Outlined.Call,
                value = telefono,
                onValueChange = { telefono = it }
            )

            CheckboxComponent() // Casilla opcional para aceptar condiciones (a futuro podría usarse para permisos)

            // --- Botón principal y autenticación con redes ---
            BottomComponent(
                textQuery = "¿Ya tienes una cuenta? ",
                textClickable = "Ingreso",
                action = "Registro",
                navController = navController,
                onGoogleClick = {
                    googleLogin(activity) // Inicia flujo de login con Google
                },
                onFacebookClick = {
                    facebookLogin(activity, listOf("email", "public_profile")) // Permisos básicos de Facebook
                },
                onPrimaryActionClick = {
                    // Registro con correo y contraseña usando FirebaseAuth y Firestore
                    authViewModel.register(
                        nombre,
                        identificacion,
                        email,
                        password,
                        telefono
                    ) { success ->
                        if (success) {
                            // Si todo sale bien, navega al Home
                            navController.navigate("home") {
                                popUpTo("signup") { inclusive = true } // Elimina Signup del stack
                            }
                        } else {
                            // Si falla, muestra error con toast
                            Toast.makeText(
                                context,
                                "Error en el registro. Intenta de nuevo",
                                Toast.LENGTH_LONG
                            ).show()
                        }
                    }
                }
            )
        }
    }
}
