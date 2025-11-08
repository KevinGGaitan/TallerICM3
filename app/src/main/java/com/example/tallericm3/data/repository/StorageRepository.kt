package com.example.tallericm3.data.repository

// Este archivo se encarga de subir imágenes de perfil a Cloudinary.
// Usa la librería oficial de Cloudinary para Android y funciones de corrutinas para manejar el proceso de forma asíncrona.
// La función principal devuelve la URL pública de la imagen subida.

import android.content.Context
import android.net.Uri
import android.util.Log
import com.cloudinary.android.MediaManager
import com.cloudinary.android.callback.ErrorInfo
import com.cloudinary.android.callback.UploadCallback
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class StorageRepository(context: Context) {

    init {
        // Se configura Cloudinary al crear la instancia del repositorio.
        // Esto se hace una sola vez para evitar múltiples inicializaciones.
        val config = mapOf(
            "cloud_name" to "dgfnsnw9i",  // nombre del espacio de almacenamiento en Cloudinary
            "api_key" to "273793978578547",  // clave pública de la cuenta
            "api_secret" to "SazRNjmwJC7NwwaaTMZ22rs8hfY"  // clave privada ,no deberia sibirse a git pero bueno xd
        )

        MediaManager.init(context, config) // Inicializa Cloudinary con la configuración anterior
    }

    // Esta función sube una imagen a Cloudinary y devuelve su URL pública (https)
    // Usa suspendCancellableCoroutine para pausar la ejecución hasta recibir respuesta (éxito o error)
    suspend fun uploadProfileImage(imageUri: Uri): String = suspendCancellableCoroutine { cont ->

        // Llamada a Cloudinary para subir la imagen
        MediaManager.get().upload(imageUri)
            .option("folder", "profile_images") // Las imágenes se guardan dentro de la carpeta "profile_images"
            .callback(object : UploadCallback { // Se define un callback para manejar los eventos de subida

                override fun onStart(requestId: String?) {
                    Log.d("Cloudinary", "Upload iniciado: $requestId") // Indica que empezó la subida
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                }

                override fun onSuccess(requestId: String?, resultData: Map<*, *>?) {
                    // Cuando Cloudinary confirma la subida, el resultado contiene la URL segura
                    val url = resultData?.get("secure_url") as? String // secure_url = enlace https al archivo
                    if (url != null) {
                        Log.d("Cloudinary", "Upload exitoso: $url")
                        cont.resume(url) // Devuelve la URL y reanuda la corrutina
                    } else {
                        cont.resumeWithException(Exception("No se obtuvo URL de Cloudinary")) // Si no hay URL, lanza error
                    }
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    val description = error?.description ?: "Error desconocido"
                    Log.e("Cloudinary", "Error al subir imagen: $description")
                    cont.resumeWithException(Exception("Error al subir imagen: $description")) // Retorna error a la corrutina
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    // Este método se ejecuta si la subida se reprograma (por red o servidor)
                    val description = error?.description ?: "Reprogramación desconocida"
                    Log.w("Cloudinary", "Upload reprogramado: $description")
                }
            })
            .dispatch() // Envía efectivamente la solicitud a Cloudinary para ejecutarse
    }
}
