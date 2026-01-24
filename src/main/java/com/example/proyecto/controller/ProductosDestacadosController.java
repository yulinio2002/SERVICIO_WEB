package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.ProductosDestacados;
import com.example.proyecto.domain.service.ProductosService;
import com.example.proyecto.dto.ProductoResponseDto;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ProductosDestacadosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

@RestController
@RequestMapping("/api/productosDestacados")
@RequiredArgsConstructor
public class ProductosDestacadosController {
    private final ProductosDestacadosRepository destacadosRepository;
    private final ProductosService productosService;

    @PostMapping
    public ResponseEntity<Boolean> addProducto(@RequestBody Long request) {
        try {
            // 1. Verificar si el producto existe
            productosService.getById(request);

            // 2. Verificar si ya está destacado
            if (destacadosRepository.existsByIdProducto(request)) {
                // Lanzar una excepción o retornar false
                return ResponseEntity.status(HttpStatus.CONFLICT).body(false);
            }

            // 3. Agregar
            ProductosDestacados destacado = new ProductosDestacados();
            destacado.setIdProducto(request);
            destacadosRepository.save(destacado);

            return ResponseEntity.status(HttpStatus.CREATED).body(true);

        } catch (ResourceNotFoundException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }
    }

    @GetMapping
    public ResponseEntity<List<ProductoResponseDto>> getDestacados() {
        List<ProductosDestacados> destacados = destacadosRepository.findAll();
        // Buscar los productos completos para cada destacado
       List<ProductoResponseDto> productosCompletos = destacados.stream()
                .map(d -> productosService.getById(d.getIdProducto()))
                .toList();
        return ResponseEntity.ok(productosCompletos);
    }

    @DeleteMapping("/{idProducto}")
    public ResponseEntity<Boolean> removeProducto(@PathVariable Long idProducto) {
        // Buscar el producto destacado
        Optional<ProductosDestacados> productoOpt = destacadosRepository.findByIdProducto(idProducto);

        if (productoOpt.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(false);
        }

        destacadosRepository.delete(productoOpt.get());
        return ResponseEntity.ok(true);
    }
}