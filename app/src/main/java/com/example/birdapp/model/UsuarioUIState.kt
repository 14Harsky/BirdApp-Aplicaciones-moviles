package com.example.birdapp.model

data class UsuarioUIState(
    val nombre: String = "",
    val correo: String = "",
    val clave: String = "",
    val repetirClave: String = "",
    val aceptaTerminos: Boolean = false,
    val errores: UsuarioErrores = UsuarioErrores()
)
