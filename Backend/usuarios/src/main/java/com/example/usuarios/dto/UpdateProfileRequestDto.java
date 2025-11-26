package com.example.usuarios.dto;

import lombok.Data;

@Data
public class UpdateProfileRequestDto {
    private String nombre;
    private String correo;
    // Puedes agregar más campos si lo necesitas
}
