package com.example.birdapp.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.birdapp.model.AuthResponse
import com.example.birdapp.model.ChangePasswordRequest
import com.example.birdapp.model.LoginRequest
import com.example.birdapp.model.UpdateProfileRequest
import com.example.birdapp.model.Usuario
import com.example.birdapp.model.UsuarioApiService
import com.example.birdapp.model.UsuarioErrores
import com.example.birdapp.model.UsuarioUIState
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class UsuarioViewModel : ViewModel() {

    private val _estado = MutableStateFlow(UsuarioUIState())
    val estado: StateFlow<UsuarioUIState> = _estado.asStateFlow()

    private val _isSessionActive = MutableStateFlow<Boolean>(false)
    val isSessionActive: StateFlow<Boolean> = _isSessionActive.asStateFlow()

    private val _navigationEvent = MutableSharedFlow<NavigationEvent>()
    val navigationEvent = _navigationEvent.asSharedFlow()

    private val _userSession = MutableStateFlow<AuthResponse?>(null)
    private val apiService = UsuarioApiService.create()

    // Actualizar el estado
    fun onNombreChange(valor: String) {
        _estado.update { it.copy(nombre = valor, errores = it.errores.copy(nombre = null)) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(correo = valor, errores = it.errores.copy(correo = null)) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(clave = valor, errores = it.errores.copy(clave = null)) }
    }

    fun onRepetirClaveChange(valor: String) {
        _estado.update { it.copy(repetirClave = valor, errores = it.errores.copy(repetirClave = null)) }
    }

    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor) }
    }

    // Lógica API
    fun registrarUsuario() {
        if (!validarFormulario()) return
        viewModelScope.launch {
            try {
                val usuario = Usuario(nombre = _estado.value.nombre, correo = _estado.value.correo, clave = _estado.value.clave)
                apiService.register(usuario)
                _navigationEvent.emit(NavigationEvent.NavigateToLogin)
            } catch (e: Exception) {
            }
        }
    }

    fun login() {
        viewModelScope.launch {
            try {
                val loginRequest = LoginRequest(_estado.value.correo, _estado.value.clave)
                val authResponse = apiService.login(loginRequest)
                _userSession.value = authResponse
                _isSessionActive.value = true
                _estado.update { it.copy(nombre = authResponse.nombre, correo = authResponse.correo, clave = "", repetirClave = "") }
                _navigationEvent.emit(NavigationEvent.NavigateToResumen)
            } catch (e: Exception) {
            }
        }
    }

    fun logout() {
        _userSession.value = null
        _isSessionActive.value = false
    }

    fun eliminarUsuario() {
        viewModelScope.launch {
            val session = _userSession.value
            if (session != null) {
                try {
                    apiService.deleteUser(session.token, session.id)
                    logout()
                } catch (e: Exception) { /*...*/ }
            }
        }
    }

    fun precargarDatosParaEditar() {
        val currentUser = _userSession.value
        if (currentUser != null) {
            _estado.update { it.copy(nombre = currentUser.nombre, correo = currentUser.correo, errores = UsuarioErrores()) }
        }
    }

    fun actualizarPerfil() {
        if (!validarPerfil()) return
        viewModelScope.launch {
            val session = _userSession.value
            if (session != null) {
                try {
                    val updateRequest = UpdateProfileRequest(nombre = _estado.value.nombre, correo = _estado.value.correo)
                    val response = apiService.updateUser(session.token, session.id, updateRequest)
                    _userSession.value = session.copy(nombre = response.nombre, correo = response.correo)
                    _estado.update { it.copy(nombre = response.nombre, correo = response.correo) }
                } catch (e: Exception) { /*...*/ }
            }
        }
    }

    fun cambiarClave() {
        if (!validarClave()) return
        viewModelScope.launch {
            val session = _userSession.value
            val estadoActual = _estado.value
            if (session != null) {
                try {
                    val request = ChangePasswordRequest(claveActual = estadoActual.clave, claveNueva = estadoActual.repetirClave)
                    apiService.changePassword(session.token, session.id, request)
                    _estado.update { it.copy(clave = "", repetirClave = "") }
                } catch (e: Exception) { /*...*/ }
            }
        }
    }

    // Funciones de Validación
    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = UsuarioErrores(
            nombre = if (estadoActual.nombre.isBlank()) "NO PUEDE ESTAR VACÍO" else null,
            correo = if (!estadoActual.correo.contains("@")) "CORREO INVÁLIDO" else null,
            clave = if (estadoActual.clave.length < 8) "DEBE TENER AL MENOS 8 CARACTERES" else null,
            repetirClave = if (estadoActual.clave != estadoActual.repetirClave) "LAS CONTRASEÑAS NO COINCIDEN" else null
        )
        val hayErrores = listOfNotNull(errores.nombre, errores.correo, errores.clave, errores.repetirClave).isNotEmpty()
        _estado.update { it.copy(errores = errores) }
        return !hayErrores
    }

    fun validarPerfil(): Boolean {
        val estadoActual = _estado.value
        val errores = UsuarioErrores(
            nombre = if (estadoActual.nombre.isBlank()) "NO PUEDE ESTAR VACÍO" else null,
            correo = if (!estadoActual.correo.contains("@")) "CORREO INVÁLIDO" else null
        )
        val hayErrores = listOfNotNull(errores.nombre, errores.correo).isNotEmpty()
        _estado.update { it.copy(errores = it.errores.copy(nombre = errores.nombre, correo = errores.correo)) }
        return !hayErrores
    }

    fun validarClave(): Boolean {
        val estadoActual = _estado.value
        val errores = UsuarioErrores(
            clave = if (estadoActual.clave.isBlank()) "NO PUEDE ESTAR VACÍO" else null,
            repetirClave = if (estadoActual.repetirClave.length < 8) "LA NUEVA CLAVE DEBE TENER AL MENOS 8 CARACTERES" else null
        )
        val hayErrores = listOfNotNull(errores.clave, errores.repetirClave).isNotEmpty()
        _estado.update { it.copy(errores = it.errores.copy(clave = errores.clave, repetirClave = errores.repetirClave)) }
        return !hayErrores
    }
}

sealed class NavigationEvent {
    object NavigateToResumen : NavigationEvent()
    object NavigateToLogin : NavigationEvent()
}
