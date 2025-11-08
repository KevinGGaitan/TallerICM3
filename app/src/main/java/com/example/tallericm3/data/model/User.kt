package com.example.tallericm3.data.model

// Este archivo define el modelo de datos principal del usuario.
// Representa cómo se guarda y se transporta la información del usuario
// tanto en Firestore como en Realtime Database.

data class User(
    val uid: String = "",                 // Identificador único del usuario (UID de Firebase)
    val nombre: String = "",              // Nombre completo del usuario
    val identificacion: String = "",      // Documento de identidad o número de registro
    val email: String = "",               // Correo electrónico
    val telefono: String = "",            // Número de teléfono
    val latitud: Double? = null,          // Última latitud registrada
    val longitud: Double? = null,         // Última longitud registrada
    val conectado: Boolean = false,       // Indica si el usuario está conectado en tiempo real
    val photoUrl: String? = null,         // URL de la foto de perfil (puede venir de Cloudinary o Firebase)
    val password: String = ""             // Contraseña (solo se usa temporalmente al registrar, no debería almacenarse)
)
