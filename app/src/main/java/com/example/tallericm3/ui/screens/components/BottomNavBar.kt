package com.example.tallericm3.ui.screens.components

// Este archivo define una barra de navegación inferior (BottomNavBar) animada con un botón flotante (FAB)
// que permite expandir o contraer el menú de navegación entre pantallas principales y cierre de sesión.

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandHorizontally
import androidx.compose.animation.shrinkHorizontally
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Logout
import androidx.compose.material.icons.filled.Map
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.tallericm3.navigation.Routes


// Barra inferior animada con opciones de navegación y botón para cerrar sesión.
@Composable
fun BottomNavBar(
    navController: NavController,
    onLogoutClick: () -> Unit
) {
    var expanded by remember { mutableStateOf(false) } // Controla si la barra está desplegada o no

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        contentAlignment = Alignment.BottomStart // Posiciona en la esquina inferior izquierda
    ) {

        // --- Contenedor animado de la barra ---
        AnimatedVisibility(
            visible = expanded,
            enter = expandHorizontally(animationSpec = tween(250), expandFrom = Alignment.Start), // Animación al abrir
            exit = shrinkHorizontally(animationSpec = tween(250), shrinkTowards = Alignment.Start), // Animación al cerrar
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 80.dp, bottom = 20.dp) // Espacio para el FAB
        ) {
            Surface(
                tonalElevation = 12.dp,
                shadowElevation = 16.dp,
                shape = RoundedCornerShape(24.dp),
                color = Color.White,
                modifier = Modifier
                    .height(60.dp)
                    .wrapContentWidth()
                    .padding(end = 16.dp)
            ) {
                NavigationBar(
                    containerColor = Color.Transparent,
                    contentColor = Color.Black,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .height(60.dp)
                        .padding(horizontal = 8.dp)
                ) {
                    val navBackStackEntry by navController.currentBackStackEntryAsState()
                    val currentRoute = navBackStackEntry?.destination?.route

                    val items = listOf(
                        Triple(Icons.Filled.Map, "Mapa", Routes.Home.route),
                        Triple(Icons.Filled.Person, "Perfil", Routes.Profile.route)
                    )

                    // --- Items de navegación principales ---
                    items.forEach { (icon, label, route) ->
                        val isSelected = currentRoute == route

                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = icon,
                                    contentDescription = label,
                                    tint = if (isSelected) Color(0xFF2979FF) else Color.Gray // Resalta si está seleccionado
                                )
                            },
                            label = {
                                Text(
                                    text = label,
                                    fontSize = 12.sp,
                                    color = if (isSelected) Color(0xFF2979FF) else Color.Gray
                                )
                            },
                            selected = isSelected,
                            alwaysShowLabel = true,
                            onClick = {
                                // Navega solo si no está ya en la misma ruta
                                if (currentRoute != route) {
                                    navController.navigate(route) {
                                        popUpTo(Routes.Home.route) { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                                expanded = false
                            },
                            colors = NavigationBarItemDefaults.colors(
                                indicatorColor = Color.Transparent // Sin fondo resaltado
                            )
                        )
                    }

                    // --- Botón de Cerrar Sesión ---
                    NavigationBarItem(
                        icon = {
                            Icon(
                                Icons.Filled.Logout,
                                contentDescription = "Salir",
                                tint = Color(0xFFD32F2F) // Rojo para destacar acción de logout
                            )
                        },
                        label = {
                            Text(
                                "Salir",
                                fontSize = 12.sp,
                                color = Color(0xFFD32F2F)
                            )
                        },
                        selected = false,
                        alwaysShowLabel = true,
                        onClick = {
                            onLogoutClick() // Ejecuta el callback del cierre de sesión
                            expanded = false
                        },
                        colors = NavigationBarItemDefaults.colors(
                            indicatorColor = Color.Transparent
                        )
                    )
                }
            }
        }

        // --- Botón flotante (FAB) para mostrar u ocultar la barra ---
        FloatingActionButton(
            onClick = { expanded = !expanded }, // Alterna el estado expandido
            containerColor = Color(0xFF2979FF), // Azul para mantener coherencia visual
            modifier = Modifier
                .align(Alignment.BottomStart)
                .padding(start = 16.dp, bottom = 16.dp)
        ) {
            Icon(
                imageVector = if (expanded) Icons.Filled.KeyboardArrowLeft else Icons.Filled.KeyboardArrowRight,
                contentDescription = if (expanded) "Ocultar menú" else "Mostrar menú",
                tint = Color.White
            )
        }
    }
}
