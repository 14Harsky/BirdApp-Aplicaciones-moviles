package com.example.birdapp.viewmodel

import androidx.lifecycle.ViewModel
import com.example.birdapp.model.Ave
import com.example.birdapp.model.AveObservada
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class AvesObservadasViewModel : ViewModel() {
    private val _avesObservadas = MutableStateFlow<List<AveObservada>>(emptyList())
    val avesObservadas: StateFlow<List<AveObservada>> = _avesObservadas.asStateFlow()

    fun agregarAveObservada(ave: Ave, ubicacion: String, notas: String) {
        val nuevaAve = AveObservada(
            id = System.currentTimeMillis().toString(),
            nombreAve = ave.name.spanish,
            imageUrl = ave.images.main,
            ubicacion = ubicacion,
            notas = notas,
            fechaObservacion = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault()).format(Date())
        )

        _avesObservadas.update { it + nuevaAve }
    }

    fun getAveObservadaById(id: String): AveObservada? {
        return _avesObservadas.value.find { it.id == id }
    }

    fun addAveObservadaImage(id: String, imageUrl: String) {
        _avesObservadas.update { currentList ->
            currentList.map {
                if (it.id == id) {
                    it.copy(userImages = it.userImages + imageUrl)
                } else {
                    it
                }
            }
        }
    }
}