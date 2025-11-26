package com.example.usuarios.dto;

import lombok.Data;

@Data
public class RegisterRequestDto {
    private String nombre;
    private String correo;
    private String password;
}
