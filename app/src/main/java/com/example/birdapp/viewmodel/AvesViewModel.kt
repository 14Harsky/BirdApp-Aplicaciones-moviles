package com.example.birdapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.birdapp.model.Ave
import com.example.birdapp.model.AvesApiService

class AvesViewModel : ViewModel() {
    private val _aves = MutableStateFlow<List<Ave>>(emptyList())
    val aves: StateFlow<List<Ave>> = _aves

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val apiService = AvesApiService.create()

    init {
        cargarAves()
    }

    fun cargarAves() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                val avesList = apiService.getAves()
                _aves.value = avesList
            } catch (e: Exception) {
                // Manejar error
                e.printStackTrace()
            } finally {
                _isLoading.value = false
            }
        }
    }
}