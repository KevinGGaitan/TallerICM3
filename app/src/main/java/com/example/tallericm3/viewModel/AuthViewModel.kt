package com.example.tallericm3.viewModel

// Este ViewModel maneja toda la lógica de autenticación:
// registro, login (correo, Google y Facebook) y logout.
// También crea o actualiza los datos del usuario en Firestore y Realtime Database.

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallericm3.data.model.User
import com.example.tallericm3.data.repository.AuthRepository
import com.example.tallericm3.data.repository.UserRepository
import kotlinx.coroutines.launch

class AuthViewModel(
    private val authRepository: AuthRepository = AuthRepository(), // Encapsula FirebaseAuth
    private val userRepository: UserRepository = UserRepository()   // Encapsula Firestore
) : ViewModel() {

    /**
     * Registro con correo y contraseña:
     * 1. Crea el usuario en Firebase Authentication
     * 2. Guarda sus datos en Firestore como documento asociado al UID
     */
    fun register(
        nombre: String,
        identificacion: String,
        email: String,
        password: String,
        telefono: String,
        latitud: Double? = null,
        longitud: Double? = null,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                // Crear usuario en FirebaseAuth (retorna UID)
                val uid = authRepository.registerUserWithEmail(email, password)

                // Construye objeto User con los datos básicos
                val user = User(
                    uid = uid,
                    nombre = nombre,
                    identificacion = identificacion,
                    email = email,
                    telefono = telefono,
                    latitud = latitud,
                    longitud = longitud,
                    conectado = false,
                    password = password
                )
                // Guarda el usuario en Firestore
                userRepository.createUser(user)

                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    /**
     * Login tradicional con email y contraseña
     * Si las credenciales son correctas, FirebaseAuth mantiene la sesión activa.
     */
    fun login(email: String, password: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                authRepository.login(email, password) // Autentica con FirebaseAuth
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    /**
     * Login con Facebook:
     * 1. Usa el token de acceso para autenticar con Firebase
     * 2. Si el usuario no existe en Firestore, lo crea con datos básicos
     */
    fun loginWithFacebook(token: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            try {
                val uid = authRepository.loginWithFacebook(token) // Devuelve UID autenticado
                val existingUser = userRepository.getUser(uid)

                // Si no hay registro previo, crear uno nuevo
                if (existingUser == null) {
                    val email = authRepository.getCurrentUserEmail() ?: ""
                    val newUser = User(
                        uid = uid,
                        nombre = "",
                        identificacion = "",
                        email = email,
                        telefono = "",
                        latitud = null,
                        longitud = null,
                        conectado = false
                    )
                    userRepository.createUser(newUser)
                }

                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    /**
     * Registro/login con Google:
     * Si el usuario no existe en Firestore, lo crea con los datos que provee Google Sign-In.
     */
    fun registerGoogleUser(
        uid: String,
        email: String,
        name: String,
        phone: String,
        onResult: (Boolean) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val existingUser = userRepository.getUser(uid)
                if (existingUser == null) {
                    val newUser = User(
                        uid = uid,
                        nombre = name,
                        identificacion = "",
                        email = email,
                        telefono = phone,
                        latitud = null,
                        longitud = null,
                        conectado = false
                    )
                    userRepository.createUser(newUser)
                }
                onResult(true)
            } catch (e: Exception) {
                e.printStackTrace()
                onResult(false)
            }
        }
    }

    /**
     * Cierra sesión y limpia los datos locales del usuario.
     * - Marca desconectado en Firestore
     * - Elimina nodo en Realtime Database
     * - Cierra sesión en FirebaseAuth
     */
    fun logout(
        userViewModel: UserViewModel,
        onResult: () -> Unit = {}
    ) {
        viewModelScope.launch {
            try {
                // Marcar desconectado en Firestore
                userViewModel.setConnectionStatus(false)

                // Borrar usuario del nodo connected_users en Realtime Database
                val realtimeRepo = com.example.tallericm3.data.repository.RealtimeRepository()
                realtimeRepo.disconnectUser()

                // Cerrar sesión de FirebaseAuth
                authRepository.logout()

                // Limpiar el usuario actual en memoria
                userViewModel.clearUser()

                onResult()
            } catch (e: Exception) {
                e.printStackTrace()
                onResult()
            }
        }
    }

}
