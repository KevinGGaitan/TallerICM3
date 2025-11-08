package com.example.tallericm3.viewModel

// Este archivo define una fábrica personalizada (Factory) para crear instancias de MapViewModel.
// Es necesaria porque MapViewModel requiere parámetros en su constructor (no puede crearse con el constructor vacío por defecto).

import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.tallericm3.data.repository.RealtimeRepository

class MapViewModelFactory(
    private val app: Application, // Contexto global de la aplicación
    private val realtimeRepository: RealtimeRepository, // Repositorio que maneja Firebase Realtime Database
    private val userViewModel: UserViewModel // Referencia al UserViewModel para sincronizar datos del usuario
) : ViewModelProvider.Factory { // Implementa una interfaz que permite crear ViewModels de forma controlada

    // Esta función se llama automáticamente cuando se solicita una instancia de ViewModel al ViewModelProvider
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        // Verifica que el tipo solicitado sea el mismo que MapViewModel o una subclase de él
        if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") // Suprime una advertencia de conversión genérica insegura
            return MapViewModel(app, realtimeRepository, userViewModel) as T // Crea la instancia del ViewModel con los parámetros requeridos
        }
        // Si se solicita un tipo diferente, lanza una excepción clara
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
