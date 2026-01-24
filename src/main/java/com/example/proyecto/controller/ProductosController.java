package com.example.proyecto.controller;

import com.example.proyecto.dto.ProductoRequestDto;
import com.example.proyecto.dto.ProductoResponseDto;
import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.domain.service.ProductosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductosController {

    private final ProductosService productosService;

    @PostMapping
    public ResponseEntity<ProductoResponseDto> create(@RequestBody ProductoRequestDto request) {
        System.out.println("CONTROLLER request body: " + request);
        return ResponseEntity.status(HttpStatus.CREATED).body( productosService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productosService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDto>> list(
            @RequestParam(required = false) Categorias categoria,
            @RequestParam(required = false) String marca
    ) {
        return ResponseEntity.ok(productosService.list(categoria, marca));
    }

    @PutMapping("/{id}")
    public ResponseEntity<ProductoResponseDto> update(@PathVariable Long id, @RequestBody ProductoRequestDto request) {
        return ResponseEntity.ok(productosService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productosService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
