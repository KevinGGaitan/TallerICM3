package com.example.tallericm3.utils

// Esta función configura el flujo de autenticación con Google en Jetpack Compose.
// Se encarga de iniciar el intent de login, procesar el resultado y autenticar con Firebase.
// Además, crea el usuario en Firestore si no existe.

import android.app.Activity
import android.util.Log
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import com.example.tallericm3.viewModel.AuthViewModel
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.GoogleAuthProvider

@Composable
fun SetupGoogleLogin(navController: NavHostController): (Activity) -> Unit {
    val authViewModel: AuthViewModel = viewModel() // Obtiene una instancia del AuthViewModel

    // Crea un launcher que inicia el intent de Google y maneja su resultado.
    // rememberLauncherForActivityResult mantiene su estado entre recomposiciones.
    val launcher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        val task = GoogleSignIn.getSignedInAccountFromIntent(result.data) // Recupera la cuenta seleccionada
        try {
            val account = task.result
            val credential = GoogleAuthProvider.getCredential(account.idToken, null) // Crea las credenciales de Firebase con el token de Google
            FirebaseAuth.getInstance().signInWithCredential(credential) // Autentica con Firebase
                .addOnSuccessListener { authResult ->
                    val user = authResult.user
                    if (user != null) {
                        Log.d("GoogleLogin", "Usuario autenticado: ${user.email}")

                        // Si el usuario no existe en Firestore, lo registra automáticamente
                        authViewModel.registerGoogleUser(
                            uid = user.uid,
                            email = user.email ?: "",
                            name = user.displayName ?: "",
                            phone = user.phoneNumber ?: ""
                        ) { success ->
                            if (success) {
                                // Navega a Home y limpia el backstack (evita volver al login)
                                navController.navigate("home") {
                                    popUpTo("login") { inclusive = true }
                                }
                            } else {
                                Log.e("GoogleLogin", "Error al registrar en Firestore")
                            }
                        }
                    }
                }
                .addOnFailureListener {
                    Log.e("GoogleLogin", "Error autenticando con Firebase: ${it.message}")
                }
        } catch (e: Exception) {
            Log.e("GoogleLogin", "Error: ${e.message}")
        }
    }

    // Devuelve una lambda que lanza el flujo de inicio de sesión con Google.
    // Se configura GoogleSignInOptions para obtener el token necesario para Firebase.
    return { activity ->
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("63496598801-a7mqkmlso6h04r928fd5b5pt06qalviq.apps.googleusercontent.com") // ID del cliente OAuth 2.0
            .requestEmail()
            .build()

        val googleSignInClient: GoogleSignInClient = GoogleSignIn.getClient(activity, gso)
        launcher.launch(googleSignInClient.signInIntent) // Inicia el intent de Google Sign-In
    }
}
