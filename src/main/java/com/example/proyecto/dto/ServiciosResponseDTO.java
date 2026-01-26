package com.example.proyecto.dto;

import com.example.proyecto.domain.entity.Fotos;
import com.example.proyecto.domain.entity.Servicios;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@RequiredArgsConstructor
public class ServiciosResponseDTO {
    private Long id;
    private String nombre;
    private String descripcion;
    private String content;
    private String features;
    private List<Images> fotos;


    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Images {
        private Long id;
        private String url;
        private String alt;
    }

    public ServiciosResponseDTO mapServiciosToDTO(Servicios servicio) {
        ServiciosResponseDTO dto = new ServiciosResponseDTO();
        dto.setId(servicio.getId());
        dto.setNombre(servicio.getNombre());
        dto.setDescripcion(servicio.getDescripcion());
        dto.setContent(servicio.getContent());
        dto.setFeatures(servicio.getFeatures());

        List<Images> imageList = new ArrayList<>();
        for (Fotos foto : servicio.getFotos()) {
            Images img = new Images();
            img.setId(foto.getId());
            img.setUrl(foto.getImagenUrl());
            img.setAlt(foto.getAlt());
            imageList.add(img);
        }
        dto.setFotos(imageList);

        return dto;
    }
}