package com.example.proyecto.dto;

import com.example.proyecto.domain.enums.Categorias;
import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@Builder
public class ProductoResponseDto {
    private Long id;
    private String nombre;
    private String marca;
    private String img_url;
    private String descripcion;
    private String content;

    private List<String> features; // lista para frontend

    private Set<Categorias> categorias;
}
