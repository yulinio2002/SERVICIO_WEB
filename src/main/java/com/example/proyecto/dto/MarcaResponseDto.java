package com.example.proyecto.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MarcaResponseDto {
    private Long id;
    private String nombre;
    private String imagenUrl;
}