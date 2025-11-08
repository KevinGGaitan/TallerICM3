package com.example.tallericm3.viewModel

// Este ViewModel controla el rastreo en tiempo real, la posición de cámara del mapa,
// los marcadores y la conexión del usuario con Firebase Realtime Database.

import android.Manifest
import android.annotation.SuppressLint
import android.app.Application
import android.content.Context
import android.content.pm.PackageManager
import android.os.Looper
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ProcessLifecycleOwner
import androidx.lifecycle.viewModelScope
import com.example.tallericm3.data.repository.RealtimeRepository
import com.google.android.gms.location.*
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.launch
import androidx.lifecycle.DefaultLifecycleObserver
import androidx.lifecycle.LifecycleOwner

class MapViewModel(
    app: Application,
    private val realtimeRepository: RealtimeRepository = RealtimeRepository(), // Repositorio de Firebase
    private val userViewModel: UserViewModel = UserViewModel() // ViewModel del usuario para sincronizar estado
) : AndroidViewModel(app) {

    init {
        // Detecta si la app entra en background y marca al usuario como desconectado.
        // Se usa ProcessLifecycleOwner para observar el ciclo de vida de toda la app, no solo de una Activity.
        ProcessLifecycleOwner.get().lifecycle.addObserver(
            object : DefaultLifecycleObserver {
                override fun onStop(owner: LifecycleOwner) { // Se dispara cuando la app se minimiza o cierra
                    viewModelScope.launch {
                        try {
                            realtimeRepository.disconnectUser() // Marca al usuario como desconectado en Firebase
                            userViewModel.setConnectionStatus(false) // Actualiza el estado local del usuario
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        )
    }

    // ====== Contexto y FusedLocationClient ======
    private val context = getApplication<Application>().applicationContext
    private val fusedLocationClient = LocationServices.getFusedLocationProviderClient(context) // API de ubicación de Google
    private var locationCallback: LocationCallback? = null // Escucha las actualizaciones de ubicación

    // ====== Estados observables principales ======
    val isConnected = mutableStateOf(false) // Estado de conexión del usuario
    val hasPermission = mutableStateOf(false) // Permiso de ubicación

    // ====== Estado del mapa (persistente mientras viva el ViewModel) ======
    val cameraPosition = mutableStateOf(LatLng(4.60971, -74.08175)) // Posición inicial (Bogotá)
    val cameraZoom = mutableFloatStateOf(12f) // Zoom inicial
    val currentMarker = mutableStateOf<Pair<Double, Double>?>(null) // Coordenadas actuales del marcador principal

    val markerImageUrl = mutableStateOf<String?>(null) // Imagen del marcador (foto del usuario)
    val markerName = mutableStateOf<String?>(null) // Nombre del usuario mostrado en el marcador

    val routePoints = mutableStateListOf<Pair<Double, Double>>() // Lista de puntos de la ruta recorrida

    // ====== Ubicación actual ======
    var currentLocation: Pair<Double, Double>? = null
        private set // Solo puede modificarse internamente

    // ====== Funciones de estado del mapa ======
    fun updateCameraPosition(latLng: LatLng, zoom: Float) {
        // Guarda la posición y el nivel de zoom actuales del mapa
        cameraPosition.value = latLng
        cameraZoom.value = zoom
    }

    /** Comprueba permisos de ubicación */
    fun hasLocationPermission(context: Context): Boolean {
        // Verifica permisos FINE y COARSE. Si uno de los dos está concedido, retorna true.
        val fine = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION)
        val coarse = ContextCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION)
        return fine == PackageManager.PERMISSION_GRANTED || coarse == PackageManager.PERMISSION_GRANTED
    }

    /**
     * Inicia actualizaciones de ubicación y sincroniza con Firebase.
     * Mantiene los datos actualizados en tiempo real mientras la app esté conectada.
     */
    @SuppressLint("MissingPermission") // Se omite la advertencia porque el permiso ya se valida manualmente
    fun startLocationUpdates() {
        if (!hasLocationPermission(context)) {
            hasPermission.value = false
            return // Detiene si no hay permisos
        }

        hasPermission.value = true
        isConnected.value = true
        userViewModel.setConnectionStatus(true) // Marca al usuario como conectado

        // Configura la frecuencia y precisión de las actualizaciones
        val request = LocationRequest.Builder(
            Priority.PRIORITY_HIGH_ACCURACY, 4000L // Cada 4 segundos
        ).setMinUpdateDistanceMeters(2f).build() // Solo si se movió más de 2 metros

        if (locationCallback != null) return // Evita crear múltiples callbacks activos

        // Callback que recibe las actualizaciones de ubicación
        locationCallback = object : LocationCallback() {
            override fun onLocationResult(result: LocationResult) {
                result.lastLocation?.let { loc ->
                    val lat = loc.latitude
                    val lon = loc.longitude
                    currentLocation = lat to lon // Guarda la ubicación actual
                    currentMarker.value = lat to lon // Actualiza el marcador principal

                    // Agrega el punto a la lista solo si es nuevo
                    if (routePoints.lastOrNull() != lat to lon) {
                        routePoints.add(lat to lon)
                    }

                    // Sincroniza la ubicación en Firebase
                    viewModelScope.launch {
                        val user = userViewModel.currentUser.value
                        markerImageUrl.value = user?.photoUrl // Guarda foto para marcador persistente
                        markerName.value = user?.nombre
                        try {
                            if (user != null) {
                                realtimeRepository.updateUserRealtimeData(user, lat, lon, true)
                            } else {
                                realtimeRepository.updateUserLocation(lat, lon, true)
                            }
                        } catch (e: Exception) {
                            e.printStackTrace()
                        }
                    }
                }
            }
        }

        // Inicia las actualizaciones de ubicación en el hilo principal
        fusedLocationClient.requestLocationUpdates(
            request,
            locationCallback!!,
            Looper.getMainLooper()
        )
    }

    /** Detiene actualizaciones (localmente) */
    fun stopLocationUpdates() {
        // Remueve el listener activo de ubicación para ahorrar batería
        locationCallback?.let {
            fusedLocationClient.removeLocationUpdates(it)
            locationCallback = null
        }
    }

    /** Desconecta completamente y limpia estado */
    fun disconnect() {
        stopLocationUpdates() // Detiene el rastreo
        isConnected.value = false
        routePoints.clear() // Limpia los puntos de la ruta
        currentMarker.value = null
        currentLocation = null

        // Actualiza Firebase para marcar desconexión
        viewModelScope.launch {
            try {
                realtimeRepository.disconnectUser()
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        userViewModel.setConnectionStatus(false)
    }

    /** Observa usuarios conectados en tiempo real */
    fun observeUsers(onUpdate: (List<Map<String, Any>>) -> Unit) {
        // Suscribe a los cambios en la lista de usuarios conectados en Firebase Realtime Database
        realtimeRepository.observeConnectedUsers { users ->
            val filtered = users.values.filter { (it["conectado"] as? Boolean) == true }
            onUpdate(filtered.ifEmpty { emptyList() }) // Llama al callback con la lista actualizada
        }
    }

    override fun onCleared() {
        super.onCleared()
        stopLocationUpdates() // Detiene la ubicación al destruir el ViewModel
    }
}
