package com.example.proyecto.dto;

import com.example.proyecto.domain.enums.Categorias;
import lombok.Data;

import java.math.BigDecimal;

@Data
public class ServicioDTO {
    private Long id;
    private String title;
    private String image;
}
