package com.example.proyecto.controller;

import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.domain.service.ServiciosService;
import lombok.RequiredArgsConstructor;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ServiceController {
    private final DisponibilidadService disponibilidadService;
    private final ServiciosService serviciosService;

    @GetMapping("/servicios/{id}/horarios")
    public List<DisponibilidadDTO> obtenerHorarioServicio(@PathVariable Long id) {
        return disponibilidadService.obtenerHorarioServicio(id);
    }

    @GetMapping("/servicios/categorias")
    public ResponseEntity<List<Categorias>> obtenerCategorias() {
        return ResponseEntity.ok(serviciosService.listarCategorias());
    }

}
