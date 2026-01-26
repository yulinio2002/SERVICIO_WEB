package com.example.proyecto.dto;

import com.example.proyecto.domain.entity.Fotos;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicioUpdateRequestDTO {

    private String nombre;
    private String descripcion;
    private String content;
    private String features;
    private List<Long> idImages;    // ID de las imágenes que seguirán asociadas al servicio

//    private List<GalleryImage> galleryImages;
//
//    @Data
//    @JsonIgnoreProperties(ignoreUnknown = true)
//    public static class GalleryImage {
//        private Long id;
//        private String url;
//        private String alt;
//    }
}