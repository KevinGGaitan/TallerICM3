package com.example.tallericm3.viewModel

// Este ViewModel gestiona el estado del usuario autenticado.
// Controla la carga, actualización, y sincronización de los datos del usuario
// con Firebase Authentication y Firestore (a través del UserRepository).

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.tallericm3.data.model.User
import com.example.tallericm3.data.repository.UserRepository
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class UserViewModel(
    private val userRepository: UserRepository = UserRepository()
) : ViewModel() {

    private val _currentUser = MutableStateFlow<User?>(null) // Estado interno del usuario actual
    val currentUser: StateFlow<User?> get() = _currentUser // Estado expuesto (solo lectura)

    private val auth = FirebaseAuth.getInstance() // Referencia a la autenticación de Firebase

    init {
        // Carga automática del usuario actual al crear el ViewModel
        auth.addAuthStateListener { firebaseAuth ->
            val firebaseUser = firebaseAuth.currentUser
            if (firebaseUser != null) {
                // Si hay sesión activa, carga los datos de Firestore
                loadCurrentUser()
            } else {
                // Si no hay usuario, limpia el estado local
                _currentUser.value = null
            }
        }
    }

    /** Carga los datos del usuario actual desde Firestore (si está autenticado) */
    fun loadCurrentUser() {
        val uid = userRepository.getCurrentUserId()
        if (uid != null) {
            viewModelScope.launch {
                try {
                    val user = userRepository.getUser(uid) // Obtiene los datos desde Firestore
                    _currentUser.value = user
                } catch (e: Exception) {
                    e.printStackTrace()
                    _currentUser.value = null
                }
            }
        } else {
            _currentUser.value = null
        }
    }

    /** Actualiza la información general del usuario en Firestore */
    fun updateUser(user: User) {
        viewModelScope.launch {
            try {
                userRepository.updateUser(user) // Sube los nuevos datos al repositorio
                _currentUser.value = user // Actualiza el estado local
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** Actualiza solo la ubicación del usuario */
    fun updateLocation(lat: Double, lon: Double) {
        val uid = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            try {
                userRepository.updateUserLocation(uid, lat, lon) // Envía la nueva ubicación
                // Actualiza el estado local también
                _currentUser.value = _currentUser.value?.copy(latitud = lat, longitud = lon)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** Actualiza el estado de conexión del usuario (conectado o desconectado) */
    fun setConnectionStatus(isConnected: Boolean) {
        val uid = userRepository.getCurrentUserId() ?: return
        viewModelScope.launch {
            try {
                userRepository.updateConnectionStatus(uid, isConnected)
                _currentUser.value = _currentUser.value?.copy(conectado = isConnected)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    /** Actualiza campos individuales del usuario (nombre, email, foto, etc.) */
    fun updateUserData(
        uid: String,
        nombre: String,
        identificacion: String,
        email: String,
        password: String,
        telefono: String,
        photoUrl: String?
    ) {
        viewModelScope.launch {
            val updates = mapOf(
                "nombre" to nombre,
                "identificacion" to identificacion,
                "email" to email,
                "password" to password,
                "telefono" to telefono,
                "photoUrl" to photoUrl
            )
            userRepository.updateUserFields(uid, updates)
            // Actualiza el estado local también
            _currentUser.value = _currentUser.value?.copy(
                nombre = nombre,
                identificacion = identificacion,
                email = email,
                password = password,
                telefono = telefono,
                photoUrl = photoUrl
            )
        }
    }

    /** Cierra sesión y limpia el usuario actual del estado */
    fun clearUser() {
        _currentUser.value = null
    }
}
