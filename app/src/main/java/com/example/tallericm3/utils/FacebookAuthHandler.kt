package com.example.tallericm3.utils

// Esta función configura e inicializa el flujo de autenticación con Facebook en Jetpack Compose.
// Registra el callback de LoginManager para recibir el resultado de Facebook Login,
// autentica al usuario con Firebase y lo redirige al home si todo sale bien.

import android.app.Activity
import android.util.Log
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tallericm3.MainActivity
import com.example.tallericm3.viewModel.AuthViewModel
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult

@Composable
fun SetupFacebookLogin(navController: NavHostController): (Activity, List<String>) -> Unit {
    val context = LocalContext.current
    val activity = context.findActivity() // Extensión que obtiene la Activity actual desde el contexto
    val callbackManager = MainActivity.Global.callbackManager // Usa el callbackManager global definido en MainActivity
    val authViewModel: AuthViewModel = viewModel() // ViewModel que maneja la lógica de autenticación

    // Registra el callback de Facebook solo una vez al componer este elemento.
    // LaunchedEffect garantiza que se ejecute una única vez por ciclo de composición.
    LaunchedEffect(Unit) {
        LoginManager.getInstance().registerCallback(
            callbackManager,
            object : FacebookCallback<LoginResult> {
                override fun onSuccess(result: LoginResult) {
                    val token = result.accessToken.token
                    Log.d("FBLogin", "Token: $token")

                    // Llama al ViewModel para autenticar con Firebase usando el token de Facebook
                    authViewModel.loginWithFacebook(token) { success ->
                        if (success) {
                            // Navega al home y limpia el backstack
                            navController.navigate("home") {
                                popUpTo("login") { inclusive = true }
                            }
                        } else {
                            Log.e("FBLogin", "Error al iniciar sesión con Firebase")
                        }
                    }
                }

                override fun onCancel() {
                    Log.d("FBLogin", "Cancelado por el usuario") // Si el usuario cancela el login
                }

                override fun onError(error: FacebookException) {
                    Log.e("FBLogin", "Error: ${error.message}") // Si ocurre un error con Facebook SDK
                }
            }
        )
    }

    // Devuelve la lambda que lanza el flujo de inicio de sesión de Facebook con los permisos solicitados.
    // Esta función puede llamarse directamente desde la UI para disparar el login.
    return { activityArg: Activity, permissions: List<String> ->
        LoginManager.getInstance().logInWithReadPermissions(activityArg, permissions)
    }
}
