package com.example.usuarios.dto;

import lombok.Data;

@Data
public class ChangePasswordRequestDto {
    private String claveActual;
    private String claveNueva;
}
