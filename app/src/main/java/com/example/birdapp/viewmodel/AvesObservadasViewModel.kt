package com.example.birdapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.birdapp.model.Ave
import com.example.birdapp.model.AveObservada
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AvesObservadasViewModel : ViewModel() {
    private val _avesObservadas = MutableStateFlow<List<AveObservada>>(emptyList())
    val avesObservadas: StateFlow<List<AveObservada>> = _avesObservadas

    fun agregarAveObservada(ave: Ave, ubicacion: String, notas: String) {
        val nuevaAve = AveObservada(
            id = System.currentTimeMillis().toString(),
            aveUid = ave.uid,
            nombreAve = ave.name.spanish,
            fechaObservacion = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date()),
            ubicacion = ubicacion,
            notas = notas
        )

        _avesObservadas.update { it + nuevaAve }
    }

    fun eliminarAveObservada(id: String) {
        _avesObservadas.update { it.filter { ave -> ave.id != id } }
    }
}