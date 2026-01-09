package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.Servicios;
import com.example.proyecto.domain.service.ServiciosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/servicios")
@RequiredArgsConstructor
public class ServiciosController {

    private final ServiciosService serviciosService;

    @PostMapping
    public ResponseEntity<Servicios> create(@RequestBody Servicios request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviciosService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Servicios> getById(@PathVariable Long id) {
        return ResponseEntity.ok(serviciosService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Servicios>> getAll() {
        return ResponseEntity.ok(serviciosService.getAll());
    }

    @GetMapping("/top/5")
    public ResponseEntity<List<Servicios>> top5() {
        return ResponseEntity.ok(serviciosService.top5ByIdDesc());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Servicios> update(@PathVariable Long id, @RequestBody Servicios request) {
        return ResponseEntity.ok(serviciosService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviciosService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
