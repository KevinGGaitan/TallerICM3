package com.example.tallericm3.ui.screens.home

// Pantalla principal del mapa (Home).
// Muestra la ubicación del usuario, su ruta recorrida y la de otros usuarios conectados.
// Permite conectar/desconectar el rastreo de ubicación en tiempo real.
// Guarda el estado del mapa (posición y zoom) entre reentradas y recupera usuarios activos.

import android.Manifest
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import com.example.tallericm3.navigation.Routes
import com.example.tallericm3.ui.screens.components.BottomNavBar
import com.example.tallericm3.ui.screens.home.components.CustomMapMarker
import com.example.tallericm3.viewModel.AuthViewModel
import com.example.tallericm3.viewModel.MapViewModel
import com.example.tallericm3.viewModel.UserViewModel
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.JointType
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.*

@Composable
fun HomeScreen(
    mapViewModel: MapViewModel,
    userViewModel: UserViewModel,
    authViewModel: AuthViewModel,
    navController: NavHostController
) {
    val context = LocalContext.current
    val currentUser by userViewModel.currentUser.collectAsState()

    val hasPermission by mapViewModel.hasPermission
    val isConnected by mapViewModel.isConnected

    // Estado de la cámara — se inicializa con la posición guardada en el ViewModel.
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(
            mapViewModel.cameraPosition.value,
            mapViewModel.cameraZoom.value
        )
    }

    val connectedUsers = remember { mutableStateListOf<Map<String, Any>>() } // usuarios activos en tiempo real

    // Launcher para pedir permiso de ubicación
    val permissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { granted ->
        mapViewModel.hasPermission.value = granted
        if (granted && mapViewModel.isConnected.value) {
            mapViewModel.startLocationUpdates()
        }
    }

    // Verifica permisos al iniciar
    LaunchedEffect(Unit) {
        if (!mapViewModel.hasLocationPermission(context)) {
            permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
        } else {
            mapViewModel.hasPermission.value = true
        }
    }

    // Escucha usuarios conectados en tiempo real desde Firebase Realtime DB
    LaunchedEffect(Unit) {
        mapViewModel.observeUsers { activeUsers ->
            connectedUsers.clear()
            if (activeUsers.isNotEmpty()) connectedUsers.addAll(activeUsers)
        }
    }

    // Limpia usuarios desconectados o con coordenadas nulas
    LaunchedEffect(connectedUsers) {
        connectedUsers.removeAll { user ->
            (user["latitud"] == null) ||
                    (user["longitud"] == null) ||
                    (user["conectado"] as? Boolean) == false
        }
    }

    // Cuando cambia la ubicación actual, mueve la cámara hacia ella
    LaunchedEffect(mapViewModel.currentLocation) {
        mapViewModel.currentLocation?.let { (lat, lon) ->
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(LatLng(lat, lon), 15f),
                durationMs = 1000
            )
        }
    }

    // Guarda posición y zoom del mapa cuando el usuario deja de moverlo
    LaunchedEffect(cameraPositionState.isMoving) {
        if (!cameraPositionState.isMoving) {
            val pos = cameraPositionState.position
            mapViewModel.updateCameraPosition(pos.target, pos.zoom)
        }
    }

    // Reanuda las actualizaciones si el usuario sigue conectado y tiene permisos
    LaunchedEffect(isConnected, hasPermission) {
        if (isConnected && hasPermission) {
            mapViewModel.startLocationUpdates()
        }
    }

    // Interfaz principal
    Scaffold(containerColor = Color.Transparent) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .consumeWindowInsets(innerPadding)
        ) {
            // --- Mapa principal ---
            if (hasPermission) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(isMyLocationEnabled = true)
                ) {
                    // Marcador del propio usuario (persistente)
                    mapViewModel.currentMarker.value?.let { (lat, lon) ->
                        CustomMapMarker(
                            imageUrl = mapViewModel.markerImageUrl.value,
                            fullName = mapViewModel.markerName.value ?: "Tú",
                            location = LatLng(lat, lon)
                        )
                    }

                    // Dibuja la ruta recorrida del usuario
                    if (mapViewModel.routePoints.size > 1) {
                        Polyline(
                            points = mapViewModel.routePoints.map { LatLng(it.first, it.second) },
                            color = Color(0xFF2979FF),
                            width = 8f,
                            jointType = JointType.ROUND
                        )
                    }

                    // Muestra marcadores y rutas de otros usuarios conectados
                    connectedUsers.forEach { user ->
                        val uid = user["uid"]?.toString() ?: return@forEach
                        val lat = (user["latitud"] as? Double) ?: return@forEach
                        val lon = (user["longitud"] as? Double) ?: return@forEach
                        val nombre = user["nombre"]?.toString() ?: "Usuario"
                        val photoUrl = user["photoUrl"]?.toString()

                        // Evita mostrar tu propio marcador duplicado
                        if (currentUser?.uid != uid) {
                            CustomMapMarker(
                                imageUrl = photoUrl,
                                fullName = nombre,
                                location = LatLng(lat, lon)
                            )

                            // Dibuja la ruta de cada usuario si existe
                            val route = (user["ruta"] as? List<Map<String, Double>>)
                            route?.let { points ->
                                if (points.size > 1) {
                                    Polyline(
                                        points = points.map {
                                            LatLng(it["lat"] ?: 0.0, it["lon"] ?: 0.0)
                                        },
                                        color = Color(0xFF9E9E9E),
                                        width = 6f,
                                        jointType = JointType.ROUND
                                    )
                                }
                            }
                        }
                    }
                }
            } else {
                // Si no hay permisos muestra indicador de carga
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
            }

            // --- Switch flotante para conectar/desconectar ---
            Box(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 24.dp)
            ) {
                Card(
                    shape = RoundedCornerShape(50),
                    elevation = CardDefaults.cardElevation(8.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 20.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isConnected) "🟢 Conectado" else "🔴 Desconectado",
                            style = MaterialTheme.typography.bodyLarge
                        )
                        Spacer(Modifier.width(12.dp))
                        Switch(
                            checked = isConnected,
                            onCheckedChange = { checked ->
                                mapViewModel.isConnected.value = checked
                                if (checked) {
                                    // Activa rastreo si hay permiso
                                    if (!hasPermission) {
                                        permissionLauncher.launch(Manifest.permission.ACCESS_FINE_LOCATION)
                                    } else {
                                        mapViewModel.startLocationUpdates()
                                        userViewModel.setConnectionStatus(true)
                                    }
                                } else {
                                    // Desactiva rastreo y limpia estado
                                    mapViewModel.disconnect()
                                    userViewModel.setConnectionStatus(false)
                                }
                            }
                        )
                    }
                }
            }

            // --- Barra inferior flotante (navegación + logout) ---
            Box(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .navigationBarsPadding()
                    .padding(bottom = 12.dp)
                    .padding(horizontal = 16.dp)
            ) {
                BottomNavBar(
                    navController = navController,
                    onLogoutClick = {
                        authViewModel.logout(userViewModel) {
                            navController.navigate(Routes.Login.route) {
                                popUpTo(Routes.Home.route) { inclusive = true }
                            }
                        }
                    }
                )
            }
        }
    }
}
