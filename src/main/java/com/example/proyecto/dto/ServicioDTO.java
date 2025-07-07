package com.example.proyecto.dto;

import com.example.proyecto.domain.enums.Categorias;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicioDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private BigDecimal precio;
    private boolean activo;
    private Categorias categoria;
    private Long proveedorId;
}
