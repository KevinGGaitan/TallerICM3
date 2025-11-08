package com.example.tallericm3.data.repository

// Este archivo maneja toda la autenticación de usuarios con Firebase Authentication.
// Permite registrar, iniciar sesión (correo o Facebook), cerrar sesión y obtener el usuario actual.

import com.google.firebase.auth.FacebookAuthProvider
import com.google.firebase.auth.FirebaseAuth
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val auth: FirebaseAuth = FirebaseAuth.getInstance() // Instancia principal de autenticación Firebase

    // Registra un nuevo usuario con correo y contraseña.
    // Retorna el UID del usuario creado o lanza una excepción si algo falla.
    suspend fun registerUserWithEmail(email: String, password: String): String {
        val result = auth.createUserWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw Exception("No se pudo registrar el usuario")
    }

    // Inicia sesión con correo y contraseña existentes.
    // Devuelve el UID si la autenticación fue exitosa.
    suspend fun login(email: String, password: String): String {
        val result = auth.signInWithEmailAndPassword(email, password).await()
        return result.user?.uid ?: throw Exception("No se pudo autenticar el usuario")
    }

    // Inicia sesión usando el token de acceso de Facebook.
    // Convierte el token en credenciales Firebase y autentica al usuario.
    suspend fun loginWithFacebook(token: String): String {
        val credential = FacebookAuthProvider.getCredential(token) // Convierte el token de Facebook en credencial Firebase
        val result = auth.signInWithCredential(credential).await()
        return result.user?.uid ?: throw Exception("No se pudo autenticar con Facebook")
    }

    // Cierra la sesión del usuario actual.
    fun logout() {
        auth.signOut()
    }

    // Obtiene el correo electrónico del usuario actual.
    // Retorna null si no hay nadie autenticado.
    fun getCurrentUserEmail(): String? {
        return auth.currentUser?.email
    }

}
