package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.Proyectos;
import com.example.proyecto.domain.service.ProyectosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
public class ProyectosController {

    private final ProyectosService proyectosService;

    @PostMapping
    public ResponseEntity<Proyectos> create(@RequestBody Proyectos request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(proyectosService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Proyectos> getById(@PathVariable Long id) {
        return ResponseEntity.ok(proyectosService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Proyectos>> getAll() {
        return ResponseEntity.ok(proyectosService.getAll());
    }

    @GetMapping("/top/5")
    public ResponseEntity<List<Proyectos>> top5() {
        return ResponseEntity.ok(proyectosService.top5ByIdDesc());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proyectos> update(@PathVariable Long id, @RequestBody Proyectos request) {
        return ResponseEntity.ok(proyectosService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        proyectosService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
