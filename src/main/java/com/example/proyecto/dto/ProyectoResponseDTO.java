package com.example.proyecto.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Data
@RequiredArgsConstructor
public class ProyectoResponseDTO {

    private Long id;
    private String nombre;
    private String descripcion;
    private Image foto;

    @Data
    @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Image {
        private Long id;
        private String url;
        private String alt;
    }
}