package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.Fotos;
import com.example.proyecto.domain.entity.Servicios;
import com.example.proyecto.domain.service.FileService;
import com.example.proyecto.domain.service.FotosService;
import com.example.proyecto.domain.service.ServiciosService;
import com.example.proyecto.dto.ServicioDTO;
import com.example.proyecto.dto.ServicioUpdateRequestDTO;
import com.example.proyecto.dto.ServiciosResponseDTO;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.Console;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServiciosController {

    private final ServiciosService serviciosService;
    private final FileService fileService;
    private final FotosService fotosService;

    @PostMapping(consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServicioDTO> create(
            @RequestParam("nombre") String nombre,
            @RequestParam("descripcion") String descripcion,
            @RequestParam("content") String content,
            @RequestParam(value = "features", required = false) String features,
            @RequestParam(value = "alt", required = false) List<String> alt,
            @RequestPart("files") List<MultipartFile> files // Las Fotos
    ) {
        // Construimos el DTO de request
        ServicioUpdateRequestDTO request = new ServicioUpdateRequestDTO();
        request.setNombre(nombre);
        request.setDescripcion(descripcion);
        request.setContent(content);
        request.setFeatures(features);

        // 1. Guardamos el servicio primero (Texto)
        Servicios nuevoServicio = serviciosService.create(request);

        // 2. Si vienen archivos, los guardamos asociados al ID del servicio creado
        List<Long> idFotosExistentes = new ArrayList<>();

        if (files != null && !files.isEmpty()) {
            idFotosExistentes = fileService.uploadGaleria(files, "services","service", alt, nuevoServicio.getId());
        } else {
            throw new IllegalArgumentException("Se requiere al menos una imagen para el servicio.");
        }

        // 3. Seleccionamos la primera imagen de la lista, si no hay lanzamos una excepción
        Long idFotoPrincipal = idFotosExistentes.get(0);

        // 4. Retornamos el servicio creado
        ServicioDTO servicioDTO = new ServicioDTO();
        servicioDTO.setId(nuevoServicio.getId());
        servicioDTO.setTitle(nuevoServicio.getNombre());
        servicioDTO.setImage(fotosService.getById(idFotoPrincipal).getImagenUrl());

        return ResponseEntity.status(HttpStatus.CREATED).body(servicioDTO);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servicios> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serviciosService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ServicioDTO>> getAll() {
        return ResponseEntity.ok(serviciosService.getAll());
    }

    @GetMapping("/top/5")
    public ResponseEntity<List<ServicioDTO>> top5() {
        return ResponseEntity.ok(serviciosService.top5ByIdDesc());
    }

    @PutMapping(value = "/{id}",consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServiciosResponseDTO> update(
            @PathVariable Long id,
            @RequestPart("data") String data,
            @RequestParam(value = "alt", required = false) List<String> alt,
            @RequestPart(value = "files", required = false) List<MultipartFile> files // Las Fotos nuevas
            ) {

        ServicioUpdateRequestDTO request;
        try {
            ObjectMapper mapper = new ObjectMapper();
            // Aquí convertimos el texto a Objeto nosotros mismos
            request = mapper.readValue(data, ServicioUpdateRequestDTO.class);
        } catch (Exception e) {
            throw new IllegalArgumentException("Error en el JSON de 'data': " + e.getMessage());
        }

        Servicios servicioUpdate = serviciosService.update(id, request);

        // Eliminar las fotos cuyo id no se encuentre en la lista de idImages
        servicioUpdate.getFotos().removeIf(foto -> {
            if (!request.getIdImages().contains(foto.getId())) {
                try {
                    // Borramos físico y BD
                    fileService.deleteFotoComplete(foto.getImagenUrl());
                    return true; // La borramos de la lista en memoria también
                } catch (Exception e) {
                    System.err.println("Error borrando foto: " + foto.getId());
                    return false; // No se pudo borrar, la dejamos en la lista
                }
            }
            return false; // Se conserva
        });

        // Agregar las fotos nuevas si existen usando como alt el listado recibido
        if (files != null && !files.isEmpty()) {
            fileService.uploadGaleria(files, "services","service", alt, servicioUpdate.getId());
        }
        ServiciosResponseDTO responseDTO = new ServiciosResponseDTO().mapServiciosToDTO(serviciosService.getById(id));

        return ResponseEntity.ok(responseDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // ELiminamos primero las imagenes asociadas
        Servicios servicio = serviciosService.getById(id);
        List<String> rutasImagenes = servicio.getFotos().stream().map(Fotos::getImagenUrl).toList();
        for (String ruta : rutasImagenes) {
            try {
                fileService.deleteFotoComplete(ruta);
            } catch (Exception e) {
                System.err.println("Error al eliminar la imagen: " + ruta + " - " + e.getMessage());
            }
        }
        // Luego eliminamos el servicio
        serviciosService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
