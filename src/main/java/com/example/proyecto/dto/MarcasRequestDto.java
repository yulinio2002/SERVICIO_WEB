package com.example.proyecto.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class MarcasRequestDto {
    private String nombre;
    private String imagenUrl;
}
