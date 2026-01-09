package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.Marcas;
import com.example.proyecto.domain.service.MarcasService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/marcas")
@RequiredArgsConstructor
public class MarcasController {
    private final MarcasService marcasService;

    // CREATE
    @PostMapping
    public ResponseEntity<Marcas> crear(@RequestBody Marcas marca) {
        Marcas creada = marcasService.crear(marca);
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
    public ResponseEntity<Marcas> actualizar(@PathVariable Long id, @RequestBody Marcas cambios) {
        return ResponseEntity.ok(marcasService.actualizar(id, cambios));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        marcasService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

    // Manejo simple de errores (opcional)
    @ExceptionHandler(EntityNotFoundException.class)
    public ResponseEntity<String> notFound(EntityNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<String> badRequest(IllegalArgumentException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
    }
}
