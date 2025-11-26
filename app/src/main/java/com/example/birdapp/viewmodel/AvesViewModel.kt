package com.example.birdapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.birdapp.model.Ave
import com.example.birdapp.model.AvesApiService
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AvesViewModel : ViewModel() {

    private val _aves = MutableStateFlow<List<Ave>>(emptyList())
    val aves: StateFlow<List<Ave>> = _aves.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val apiService: AvesApiService = AvesApiService.create()

    init {
        getAves()
    }

    private fun getAves() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                _aves.value = apiService.getAves()
            } catch (e: Exception) {
                // Handle error
            } finally {
                _isLoading.value = false
            }
        }
    }
}