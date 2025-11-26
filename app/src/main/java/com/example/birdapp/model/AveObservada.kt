package com.example.birdapp.model

data class AveObservada(
    val id: String,
    val nombreAve: String,
    val imageUrl: String,
    val ubicacion: String,
    val notas: String,
    val fechaObservacion: String,
    val userImages: List<String> = emptyList()
)
