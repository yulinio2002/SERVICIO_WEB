package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.Fotos;
import com.example.proyecto.domain.entity.Proyectos;
import com.example.proyecto.domain.entity.Servicios;
import com.example.proyecto.domain.service.FotosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/fotos")
@RequiredArgsConstructor
public class FotosController {

    private final FotosService fotosService;

    @PostMapping
    public ResponseEntity<Fotos> create(@RequestBody Fotos request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(fotosService.create(request));
    }

    // Extra: fotos de un servicio
    @GetMapping("/servicios/{servicioId}")
    public ResponseEntity<List<Fotos>> getByServicio(@PathVariable Long servicioId) {
        return ResponseEntity.ok(fotosService.getByServicio(servicioId));
    }

    // Extra: fotos de un proyecto
    @GetMapping("/proyectos/{proyectoId}")
    public ResponseEntity<List<Fotos>> getByProyecto(@PathVariable Long proyectoId) {
        return ResponseEntity.ok(fotosService.getByProyecto(proyectoId));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Fotos> getById(@PathVariable Long id) {
        return ResponseEntity.ok(fotosService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Fotos>> getAll() {
        return ResponseEntity.ok(fotosService.getAll());
    }

    /**
     * Opcional: endpoints convenientes para crear por servicio/proyecto
     * (sin obligarte a mandar servicio/proyecto en el body)
     */
    @PostMapping("/servicios/{servicioId}")
    public ResponseEntity<Fotos> createForServicio(@PathVariable Long servicioId, @RequestBody Fotos request) {
        Servicios s = new Servicios();
        s.setId(servicioId);
        request.setServicio(s);
        request.setProyectos(null);

        return ResponseEntity.status(HttpStatus.CREATED).body(fotosService.create(request));
    }

    @PostMapping("/proyectos/{proyectoId}")
    public ResponseEntity<Fotos> createForProyecto(@PathVariable Long proyectoId, @RequestBody Fotos request) {
        Proyectos p = new Proyectos();
        p.setId(proyectoId);
        request.setProyectos(p);
        request.setServicio(null);

        return ResponseEntity.status(HttpStatus.CREATED).body(fotosService.create(request));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Fotos> update(@PathVariable Long id, @RequestBody Fotos request) {
        return ResponseEntity.ok(fotosService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        fotosService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
