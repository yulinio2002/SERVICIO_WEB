package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.Servicios;
import com.example.proyecto.domain.service.ServiciosService;
import com.example.proyecto.dto.ServicioDTO;
import com.example.proyecto.dto.ServicioUpdateRequestDTO;
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
    public ResponseEntity<ServicioDTO> create(@RequestBody ServicioUpdateRequestDTO request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(serviciosService.create(request));
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

    @PutMapping("/{id}")
    public ResponseEntity<Servicios> update(@PathVariable Long id, @RequestBody ServicioUpdateRequestDTO request) {
        return ResponseEntity.ok(serviciosService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        serviciosService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
