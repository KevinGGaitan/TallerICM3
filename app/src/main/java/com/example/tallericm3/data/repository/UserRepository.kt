package com.example.tallericm3.data.repository

// Este archivo maneja todas las operaciones relacionadas con los usuarios en Firebase Firestore y Authentication.
// Se encarga de crear, leer, actualizar y manejar datos del usuario autenticado en la nube.

import com.example.tallericm3.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class UserRepository {

    private val firestore = FirebaseFirestore.getInstance() // Instancia principal de Firestore
    private val usersCollection = firestore.collection("users") // Colección donde se almacenan los usuarios
    private val auth = FirebaseAuth.getInstance() // Instancia de autenticación de Firebase

    // Obtiene el UID del usuario autenticado actualmente; null si no hay sesión
    fun getCurrentUserId(): String? = auth.currentUser?.uid

    // Crea un nuevo documento del usuario en Firestore (sobrescribe si ya existe)
    suspend fun createUser(user: User) {
        usersCollection.document(user.uid).set(user).await() // .await() espera la operación dentro de una corrutina
    }

    // Recupera un usuario específico por su UID desde Firestore
    suspend fun getUser(uid: String): User? {
        val snapshot = usersCollection.document(uid).get().await()
        return snapshot.toObject(User::class.java) // Convierte el documento a un objeto User
    }

    // Actualiza todos los datos de un usuario (reemplaza el documento completo)
    suspend fun updateUser(user: User) {
        usersCollection.document(user.uid).set(user).await()
    }

    // Actualiza solo la ubicación (latitud y longitud) del usuario
    suspend fun updateUserLocation(uid: String, lat: Double, lon: Double) {
        usersCollection.document(uid).update(
            mapOf(
                "latitud" to lat, // campo en Firestore
                "longitud" to lon
            )
        ).await()
    }

    // Actualiza el estado de conexión (conectado / desconectado)
    suspend fun updateConnectionStatus(uid: String, conectado: Boolean) {
        usersCollection.document(uid)
            .update("conectado", conectado)
            .await()
    }

    // Actualiza campos específicos de un usuario según el mapa recibido
    suspend fun updateUserFields(uid: String, data: Map<String, Any?>) {
        usersCollection.document(uid).update(data).await()
    }

    // Actualiza solo la URL de la foto del usuario
    suspend fun updateUserPhotoUrl(uid: String, url: String) {
        usersCollection.document(uid).update("photoUrl", url).await()
    }
}
