package com.example.proyecto.dto;


import com.example.proyecto.domain.enums.Categorias;
import lombok.Data;

import java.util.Set;

@Data
public class ProductoRequestDto {
    private String nombre;
    private String img_url;
    private String descripcion;
    private String content;

    // El front idealmente enviará lista, pero también permitimos string
    private String[] featuresList;
    private String features; // "a;b;c" opcional

    private String marca;
    private Set<Categorias> categorias;
}