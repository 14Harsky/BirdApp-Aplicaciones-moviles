package com.example.usuarios.dto;

import lombok.Data;

@Data
public class LoginRequestDto {
    private String correo;
    private String password;
}
