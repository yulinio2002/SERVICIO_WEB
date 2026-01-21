package com.example.proyecto.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ServicioUpdateRequestDTO {
    private Long id;
    private String title;
    private String slug; // no se persiste en Servicios (por ahora)
    private String description;
    private String content;
    private List<String> features;
    private List<String> images;

    private List<GalleryImage> galleryImages;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class GalleryImage {
        private Long id;
        private String url;
        private String alt;
    }
}