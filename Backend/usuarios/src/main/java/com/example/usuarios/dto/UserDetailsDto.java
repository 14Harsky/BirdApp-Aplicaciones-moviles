package com.example.usuarios.dto;

import lombok.Data;

@Data
public class UserDetailsDto {
    private int id;
    private String nombre;
    private String correo;
    // No incluir password
}
