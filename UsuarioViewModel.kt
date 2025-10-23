package com.example.birdapp.viewmodel

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.update
import com.example.birdapp.model.UsuarioUIState
import com.example.birdapp.model.UsuarioErrores

class UsuarioViewModel : ViewModel() {

    private val _estado = MutableStateFlow(UsuarioUIState())
    val estado: StateFlow<UsuarioUIState> = _estado

    fun onNombreChange(valor: String) {
        _estado.update { it.copy(
            nombre = valor,
            errores = it.errores.copy(nombre = null)
        ) }
    }

    fun onCorreoChange(valor: String) {
        _estado.update { it.copy(
            correo = valor,
            errores = it.errores.copy(correo = null)
        ) }
    }

    fun onClaveChange(valor: String) {
        _estado.update { it.copy(
            clave = valor,
            errores = it.errores.copy(clave = null)
        ) }
    }

    fun onDireccionChange(valor: String) {
        _estado.update { it.copy(
            direccion = valor,
            errores = it.errores.copy(direccion = null)
        ) }
    }

    fun onAceptarTerminosChange(valor: Boolean) {
        _estado.update { it.copy(aceptaTerminos = valor) }
    }

    fun validarFormulario(): Boolean {
        val estadoActual = _estado.value
        val errores = UsuarioErrores(
            nombre = if (estadoActual.nombre.isBlank()) "NO PUEDE ESTAR VACÍO" else null,
            correo = if (!estadoActual.correo.contains("@")) "CORREO INVÁLIDO" else null,
            clave = if (estadoActual.clave.length < 8) "DEBE TENER AL MENOS 8 CARACTERES" else null,
            direccion = if (estadoActual.direccion.isBlank()) "NO PUEDE ESTAR VACÍO" else null
        )

        val hayErrores = listOfNotNull(
            errores.nombre,
            errores.correo,
            errores.clave,
            errores.direccion
        ).isNotEmpty()

        _estado.update { it.copy(errores = errores) }
        return !hayErrores
    }

}