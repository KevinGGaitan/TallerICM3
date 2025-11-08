package com.example.tallericm3

// Archivo principal que inicializa Firebase, configura la observación del ciclo de vida
// y lanza la interfaz principal de la aplicación. Controla la conexión del usuario
// cuando la app entra o sale del foreground.

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.lifecycleScope
import com.example.tallericm3.data.repository.RealtimeRepository
import com.example.tallericm3.navigation.NavigationStack
import com.example.tallericm3.ui.theme.SimpleLoginScreenTheme
import com.example.tallericm3.viewModel.UserViewModel
import com.facebook.CallbackManager
import com.google.firebase.FirebaseApp
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {

    // Objeto global para manejar constantes compartidas
    object Global {
        var preferencias_compartidas = "shared_prefs" // Nombre del archivo de SharedPreferences
        var callbackManager = CallbackManager.Factory.create() // Necesario para login de Facebook
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Inicializa Firebase (necesario antes de cualquier llamada a Firebase)
        FirebaseApp.initializeApp(this)

        // Se crean instancias de ViewModel y repositorio para gestionar usuarios y base de datos
        val userViewModel = UserViewModel()
        val realtimeRepository = RealtimeRepository()

        // Observa el ciclo de vida de la aplicación
        ProcessLifecycleOwner.get().lifecycle.addObserver(object : DefaultLifecycleObserver {

            // Cuando la app vuelve al frente (foreground)
            override fun onStart(owner: LifecycleOwner) {
                lifecycleScope.launch {
                    try {
                        userViewModel.setConnectionStatus(true) // Marca al usuario como conectado
                        println("Usuario marcado como conectado (onStart)")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }

            // Cuando la app se minimiza o se cierra (background)
            override fun onStop(owner: LifecycleOwner) {
                lifecycleScope.launch {
                    try {
                        userViewModel.setConnectionStatus(false) // Marca al usuario como desconectado
                        realtimeRepository.disconnectUser() // Desconecta del Realtime Database
                        println("Usuario desconectado al salir o minimizar la app")
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                }
            }
        })

        // Configura el contenido de la interfaz usando Jetpack Compose
        setContent {
            SimpleLoginScreenTheme { // Aplica el tema global
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = Color.White // Fondo blanco principal
                ) {
                    NavigationStack() // Navegación principal de la app
                }
            }
        }
    }

    // Callback necesario para manejar el resultado del login con Facebook
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        Global.callbackManager.onActivityResult(requestCode, resultCode, data) // Redirige el resultado
    }
}
