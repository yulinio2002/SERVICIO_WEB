package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.Marcas;
import com.example.proyecto.domain.service.FileService;
import com.example.proyecto.domain.service.MarcasService;
import com.example.proyecto.dto.MarcasRequestDto;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
@RequiredArgsConstructor
public class MarcasController {
    private final MarcasService marcasService;
    private final FileService fileService;

    // CREATE
    @PostMapping
    public ResponseEntity<Marcas> crear(
            @RequestParam("file") MultipartFile file,
            @RequestParam("nombre") String nombre) {
        System.out.println("Creando marca: " + nombre);
        // 1. Crear entidad Marca sin imagen
        Marcas marca = new Marcas();
        marca.setNombre(nombre);
        Marcas creada = marcasService.crear(marca);
        // 2. Subir imagen y actualizar URL
        try {
            if (file != null && !file.isEmpty()) {
                var foto = fileService.uploadFoto(file, "brands", "Logo de " + nombre, "brands", creada.getId());
                creada.setImagenUrl(foto.getImagenUrl());
                creada = marcasService.actualizar(creada.getId(), new MarcasRequestDto(creada.getNombre(), creada.getImagenUrl()));
            }
        } catch (Exception e) {
            e.printStackTrace();
            throw new RuntimeException("Error al subir la imagen de la marca: " + e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Marcas>> listar() {
        return ResponseEntity.ok(marcasService.listar());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Marcas> obtener(@PathVariable Long id) {
        return ResponseEntity.ok(marcasService.obtenerPorId(id));
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Marcas> actualizar(
            @PathVariable Long id,
            @RequestParam(value = "file", required = false) MultipartFile file,
            @RequestParam(value = "nombre", required = false) String nombre) {

        // 1. Buscamos la marca existente
        Marcas marcaActual = marcasService.obtenerPorId(id); // Asumo que esto lanza excepción si no existe

        // 2. Actualizamos el nombre si viene en la petición
        if (nombre != null && !nombre.isEmpty()) {
            marcaActual.setNombre(nombre);
        }

        // 3. Actualizamos la imagen si viene un archivo nuevo
        if (file != null && !file.isEmpty()) {
            try {
                // A) Borrar la imagen anterior si existe
                if (marcaActual.getImagenUrl() != null && !marcaActual.getImagenUrl().isEmpty()) {
                    fileService.deleteFotoComplete(marcaActual.getImagenUrl());
                }

                // B) Subir la nueva imagen
                var foto = fileService.uploadFoto(file, "brands", "Logo de " + marcaActual.getNombre(), "brands", id);

                // C) Actualizar la URL en la entidad Marca
                marcaActual.setImagenUrl(foto.getImagenUrl());

                } catch (Exception e) {
                    throw new RuntimeException("Error al actualizar la imagen: " + e.getMessage());
                }
        }

        // 4. Guardamos los cambios
        MarcasRequestDto dto = new MarcasRequestDto(marcaActual.getNombre(), marcaActual.getImagenUrl());
        Marcas marcaGuardada = marcasService.actualizar(id, dto);

        return ResponseEntity.ok(marcaGuardada);
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        Marcas marca = marcasService.obtenerPorId(id);
        try{
            if (marca.getImagenUrl() != null && !marca.getImagenUrl().isEmpty()) {
                fileService.deleteFotoComplete(marca.getImagenUrl());
            }
        }
        catch (Exception e) {
            throw new RuntimeException("Error al eliminar la imagen: " + e.getMessage());
        }
        marcasService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
