package com.example.tallericm3.data.repository

// Este archivo maneja la sincronización de usuarios conectados en tiempo real usando Firebase Realtime Database.
// Guarda la ubicación, estado de conexión y datos básicos de cada usuario mientras la app está activa.

import com.example.tallericm3.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import kotlinx.coroutines.tasks.await

class RealtimeRepository {
    private val auth = FirebaseAuth.getInstance() // Autenticación actual de Firebase
    private val database = FirebaseDatabase.getInstance().reference // Referencia raíz de la base de datos

    // Actualiza o crea el nodo del usuario actual en "connected_users"
    // Incluye sus datos básicos, ubicación y estado de conexión.
    // Usa onDisconnect() para eliminar automáticamente al usuario si se pierde la conexión o la app se cierra.
    suspend fun updateUserRealtimeData(user: User, lat: Double, lon: Double, connected: Boolean) {
        val uid = user.uid
        val userRef = database.child("connected_users").child(uid)

        val userData = mapOf(
            "uid" to uid,
            "nombre" to (user.nombre.ifEmpty { "Usuario" }), // Si el nombre está vacío, se usa "Usuario"
            "photoUrl" to (user.photoUrl ?: ""), // Puede ser null, por eso el operador Elvis
            "latitud" to lat,
            "longitud" to lon,
            "conectado" to connected
        )

        userRef.setValue(userData).await() // Envía los datos a Firebase

        userRef.onDisconnect().removeValue() // Limpieza automática cuando el socket se desconecta
    }

    // Solo actualiza la ubicación del usuario (latitud, longitud y estado de conexión).
    // Mantiene el onDisconnect() activo para eliminar el nodo en caso de desconexión.
    suspend fun updateUserLocation(lat: Double, lon: Double, connected: Boolean) {
        val uid = auth.currentUser?.uid ?: return
        val userRef = database.child("connected_users").child(uid)

        val locationData = mapOf(
            "latitud" to lat,
            "longitud" to lon,
            "conectado" to connected
        )

        userRef.updateChildren(locationData).await()
        userRef.onDisconnect().removeValue()
    }

    // Elimina manualmente al usuario de la lista de conectados
    suspend fun disconnectUser() {
        val uid = auth.currentUser?.uid ?: return
        database.child("connected_users").child(uid).removeValue().await()
    }

    // Escucha los cambios en la lista de usuarios conectados en tiempo real.
    // Cada vez que alguien se conecta o desconecta, el callback onUpdate recibe el nuevo mapa.
    fun observeConnectedUsers(onUpdate: (Map<String, Map<String, Any>>) -> Unit) {
        database.child("connected_users")
            .addValueEventListener(object : com.google.firebase.database.ValueEventListener {

                override fun onDataChange(snapshot: com.google.firebase.database.DataSnapshot) {
                    if (!snapshot.exists()) {
                        onUpdate(emptyMap()) // Si no hay usuarios, limpia el mapa
                        return
                    }

                    // Convierte los hijos en un mapa de usuarios activos
                    val users = snapshot.children.mapNotNull { child ->
                        val data = child.value as? Map<String, Any> ?: return@mapNotNull null
                        val conectado = data["conectado"] as? Boolean ?: false
                        if (conectado) child.key!! to data else null
                    }.toMap()

                    onUpdate(users)
                }

                override fun onCancelled(error: com.google.firebase.database.DatabaseError) {
                    println("Error al escuchar usuarios conectados: ${error.message}")
                }
            })
    }

    // Actualiza la foto del usuario en la base de datos en tiempo real.
    // También mantiene el onDisconnect() para eliminar el nodo si se cierra la sesión.
    suspend fun updateUserPhoto(photoUrl: String) {
        val uid = auth.currentUser?.uid ?: return
        val userRef = database.child("connected_users").child(uid)

        userRef.child("photoUrl").setValue(photoUrl).await()
        userRef.onDisconnect().removeValue()
    }
}
