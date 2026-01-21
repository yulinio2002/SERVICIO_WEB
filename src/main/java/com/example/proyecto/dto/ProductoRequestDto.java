package com.example.proyecto.dto;

import com.example.proyecto.domain.enums.Categorias;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ProductoRequestDto {

    // --- Back-end (modelo actual)
    private String nombre;
    private String img_url;
    private String descripcion;
    private String content;

    // features en back: lista o "a;b;c"
    private String[] featuresList;
    private String features; // "a;b;c" opcional

    private String marca;
    private Set<Categorias> categorias;

    // --- Front-end (payload tipo servicios)
    private Long id;
    private String title;
    private String description;

}