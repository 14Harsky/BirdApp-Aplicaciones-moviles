package com.example.usuarios.dto;

import lombok.Data;

@Data
public class AuthResponseDto {
    private String token;
    private int id;
    private String nombre;
    private String correo;
}
