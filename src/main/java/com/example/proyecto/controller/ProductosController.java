package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.Productos;
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
    public ResponseEntity<Productos> create(@RequestBody Productos request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productosService.create(request));
    }

    @GetMapping("/{id}")
    public ResponseEntity<Productos> getById(@PathVariable Long id) {
        return ResponseEntity.ok(productosService.getById(id));
    }

    /**
     * GET /api/productos
     * - ?categoria=...
     * - ?marca=...
     * - ?orden=categoria|marca
     */
    @GetMapping
    public ResponseEntity<List<Productos>> list(
            @RequestParam(required = false) Categorias categoria,
            @RequestParam(required = false) String marca,
            @RequestParam(required = false) String orden
    ) {
        return ResponseEntity.ok(productosService.list(categoria, marca, orden));
    }

    @GetMapping("/top/5")
    public ResponseEntity<List<Productos>> top5() {
        return ResponseEntity.ok(productosService.top5ByIdDesc());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Productos> update(@PathVariable Long id, @RequestBody Productos request) {
        return ResponseEntity.ok(productosService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        productosService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
