package com.example.tallericm3.ui.screens.profile

// Pantalla para editar la información del perfil del usuario.
// Permite actualizar nombre, correo, teléfono, contraseña y foto de perfil.
// Maneja permisos de cámara/galería, subida de imagen al almacenamiento (Cloudinary)
// y sincronización de los datos con Firestore y Realtime Database.

import android.Manifest
import android.content.ContentValues
import android.content.Context
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.AsyncImage
import com.example.tallericm3.data.repository.StorageRepository
import com.example.tallericm3.viewModel.UserViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileEditScreen(
    navController: NavController,
    userViewModel: UserViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val storageRepository = remember { StorageRepository(context) }
    val user by userViewModel.currentUser.collectAsState()

    // Estados temporales para los campos del formulario
    var tempName by remember { mutableStateOf(user?.nombre ?: "") }
    var tempIdentificacion by remember { mutableStateOf(user?.identificacion ?: "") }
    var tempEmail by remember { mutableStateOf(user?.email ?: "") }
    var passwordVisible by remember { mutableStateOf(false) } // controla visibilidad del password
    var tempPassword by remember { mutableStateOf(user?.password ?: "") }
    var tempPhone by remember { mutableStateOf(user?.telefono ?: "") }

    // Estados relacionados con la imagen del perfil
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var photoUri by remember { mutableStateOf<Uri?>(null) }

    // --- Launchers: manejan los resultados de cámara y galería ---
    val takePictureLauncher = rememberLauncherForActivityResult(ActivityResultContracts.TakePicture()) { success ->
        if (success && photoUri != null) selectedImageUri = photoUri
    }

    val pickGalleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri ->
        if (uri != null) selectedImageUri = uri
    }

    // --- Permisos para cámara y galería ---
    val cameraPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        if (results.values.all { it }) {
            val uri = createMediaStoreImageUri(context)
            photoUri = uri
            takePictureLauncher.launch(uri)
        } else Toast.makeText(context, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
    }

    val galleryPermissionLauncher = rememberLauncherForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { results ->
        if (results.values.all { it }) pickGalleryLauncher.launch("image/*")
        else Toast.makeText(context, "Permiso de galería denegado", Toast.LENGTH_SHORT).show()
    }

    // --- Solicitud de permisos según versión de Android ---
    fun requestCameraPermissions() {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arrayOf(Manifest.permission.CAMERA)
        else arrayOf(Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE)
        cameraPermissionLauncher.launch(perms)
    }

    fun requestGalleryPermissions() {
        val perms = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU)
            arrayOf(Manifest.permission.READ_MEDIA_IMAGES)
        else arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE)
        galleryPermissionLauncher.launch(perms)
    }

    // --- UI principal ---
    Scaffold { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("Editar perfil", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(16.dp))

            // Imagen actual o seleccionada del usuario
            AsyncImage(
                model = selectedImageUri ?: user?.photoUrl,
                contentDescription = "Foto de perfil",
                modifier = Modifier
                    .size(120.dp)
                    .clip(CircleShape),
                contentScale = ContentScale.Crop
            )

            Spacer(modifier = Modifier.height(16.dp))
            // Botones para elegir entre galería o cámara
            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                Button(onClick = { requestGalleryPermissions() }) { Text("Galería") }
                Button(onClick = { requestCameraPermissions() }) { Text("Cámara") }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Campos editables del formulario
            OutlinedTextField(value = tempName, onValueChange = { tempName = it }, label = { Text("Nombre") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = tempIdentificacion, onValueChange = { tempIdentificacion = it }, label = { Text("Número de identificación") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = tempEmail, onValueChange = { tempEmail = it }, label = { Text("Correo electrónico") }, modifier = Modifier.fillMaxWidth())
            Spacer(modifier = Modifier.height(8.dp))

            // Campo de contraseña con icono para mostrar/ocultar
            OutlinedTextField(
                value = tempPassword,
                onValueChange = { tempPassword = it },
                label = { Text("Contraseña") },
                modifier = Modifier.fillMaxWidth(),
                visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                trailingIcon = {
                    val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                        Icon(imageVector = image, contentDescription = if (passwordVisible) "Ocultar contraseña" else "Mostrar contraseña")
                    }
                },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password)
            )

            Spacer(modifier = Modifier.height(8.dp))
            OutlinedTextField(value = tempPhone, onValueChange = { tempPhone = it }, label = { Text("Teléfono") }, modifier = Modifier.fillMaxWidth())

            Spacer(modifier = Modifier.height(24.dp))

            // --- Botón para guardar cambios ---
            if (isLoading) {
                CircularProgressIndicator()
            } else {
                Button(onClick = {
                    coroutineScope.launch {
                        isLoading = true
                        try {
                            var photoUrl = user?.photoUrl
                            val uid = user?.uid ?: return@launch

                            // Si hay nueva imagen, se sube al repositorio
                            selectedImageUri?.let {
                                photoUrl = storageRepository.uploadProfileImage(it)

                                // Se actualiza también en Realtime Database para que otros usuarios vean el cambio
                                val realtimeRepository = com.example.tallericm3.data.repository.RealtimeRepository()
                                realtimeRepository.updateUserPhoto(photoUrl)
                            }

                            // Actualización en Firestore + ViewModel
                            userViewModel.updateUserData(
                                uid = uid,
                                nombre = tempName,
                                identificacion = tempIdentificacion,
                                email = tempEmail,
                                password = tempPassword,
                                telefono = tempPhone,
                                photoUrl = photoUrl
                            )

                            Toast.makeText(context, "Perfil actualizado", Toast.LENGTH_SHORT).show()
                            navController.popBackStack()
                        } catch (e: Exception) {
                            Toast.makeText(context, "Error: ${e.message}", Toast.LENGTH_LONG).show()
                        } finally {
                            isLoading = false
                        }
                    }
                }) {
                    Text("Guardar cambios")
                }
            }
        }
    }
}

// Crea un URI temporal para almacenar una foto capturada desde la cámara.
private fun createMediaStoreImageUri(context: Context): Uri {
    val contentValues = ContentValues().apply {
        val timestamp = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        put(MediaStore.Images.Media.DISPLAY_NAME, "IMG_${timestamp}.jpg")
        put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg")
        put(MediaStore.Images.Media.RELATIVE_PATH, Environment.DIRECTORY_PICTURES + "/TallerICM3")
    }
    return context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contentValues)
        ?: throw IllegalStateException("No se pudo crear URI en MediaStore")
}
