package com.example.proyecto.dto;

import com.example.proyecto.domain.enums.Categorias;
import jakarta.validation.constraints.Min;
import lombok.Data;

@Data
public class FiltroServicioDTO {
    private Categorias categoria;

    @Min(value = 0, message = "El precio mínimo debe ser mayor o igual a 0")
    private Double precioMin;

    @Min(value = 0, message = "El precio máximo debe ser mayor o igual a 0")
    private Double precioMax;

    @Min(value = 0, message = "La calificación mínima debe ser mayor o igual a 0")
    private Double calificacionMin;

    @Min(value = 0, message = "La página debe ser mayor o igual a 0")
    private Integer page = 0;

    @Min(value = 1, message = "El tamaño debe ser mayor a 0")
    private Integer size = 10;

    // Getters y setters automáticos con @Data
}