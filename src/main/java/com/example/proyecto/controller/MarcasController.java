package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.Marcas;
import com.example.proyecto.domain.service.MarcasService;
import com.example.proyecto.dto.MarcasRequestDto;
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
        System.out.println("Creando marca: " + marca.getNombre());
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
    public ResponseEntity<Marcas> actualizar(@PathVariable Long id, @RequestBody MarcasRequestDto cambios) {
        return ResponseEntity.ok(marcasService.actualizar(id, cambios));
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminar(@PathVariable Long id) {
        marcasService.eliminar(id);
        return ResponseEntity.noContent().build();
    }

}
