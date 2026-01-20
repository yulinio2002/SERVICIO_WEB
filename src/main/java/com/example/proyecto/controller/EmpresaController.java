package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.Empresa;
import com.example.proyecto.domain.service.EmpresaService;
import jakarta.persistence.EntityNotFoundException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/empresas")
@RequiredArgsConstructor
public class EmpresaController {
    private final EmpresaService empresaService;
    //admin
    // CREATE
    @PostMapping
    public ResponseEntity<Empresa> crearEmpresa(@RequestBody Empresa empresa) {
        Empresa creada = empresaService.crearEmpresa(empresa);
        return ResponseEntity.status(HttpStatus.CREATED).body(creada);
    }

    // READ ALL
    @GetMapping
    public ResponseEntity<List<Empresa>> listarEmpresas() {
        return ResponseEntity.ok(empresaService.listarEmpresas());
    }

    // READ BY ID
    @GetMapping("/{id}")
    public ResponseEntity<Empresa> obtenerEmpresa(@PathVariable Long id) {
        return ResponseEntity.ok(empresaService.obtenerEmpresaPorId(id));
    }
    //admin
    // UPDATE (solo campos editables)
    @PutMapping("/{id}")
    public ResponseEntity<Empresa> actualizarEmpresa(
            @PathVariable Long id,
            @RequestBody Empresa empresaActualizada
    ) {
        Empresa actualizada = empresaService.actualizarEmpresa(id, empresaActualizada);
        return ResponseEntity.ok(actualizada);
    }
    //admin
    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> eliminarEmpresa(@PathVariable Long id) {
        empresaService.eliminarEmpresa(id);
        return ResponseEntity.noContent().build();
    }

//    // ===============================
//    // Manejo básico de errores
//    // ===============================
//    @ExceptionHandler(EntityNotFoundException.class)
//    public ResponseEntity<String> manejarNotFound(EntityNotFoundException ex) {
//        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(ex.getMessage());
//    }
//
//    @ExceptionHandler(IllegalArgumentException.class)
//    public ResponseEntity<String> manejarBadRequest(IllegalArgumentException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(ex.getMessage());
//    }
}
