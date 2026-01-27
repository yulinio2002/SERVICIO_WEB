package com.example.proyecto.controller;

import com.example.proyecto.domain.service.FileService;
import com.example.proyecto.dto.ProductoRequestDto;
import com.example.proyecto.dto.ProductoResponseDto;
import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.domain.service.ProductosService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@RestController
@RequestMapping("/api/productos")
@RequiredArgsConstructor
public class ProductosController {

    private final ProductosService productosService;
    private final FileService fileService;

    /*
     * Es necesario usar @RequestParam para recibir tanto el archivo como los demás campos del producto.
     * Los productos se deben crear con al menos una imagen, por lo que el campo 'file' es obligatorio.
     */
    @PostMapping
    public ResponseEntity<ProductoResponseDto> create(@RequestParam("file") MultipartFile file,
                                                      @RequestParam("nombre") String nombre,
                                                      @RequestParam("marca") String marca,
                                                      @RequestParam("descripcion") String descripcion,
                                                      @RequestParam("content") String content,
                                                      @RequestParam(value = "features", required = false) String features,
                                                      @RequestParam("categorias") List<Categorias> categorias) {
        ProductoRequestDto request = new ProductoRequestDto();
        request.setNombre(nombre);
        request.setMarca(marca);
        request.setDescripcion(descripcion);
        request.setContent(content);
        request.setFeatures(features);
        request.setCategorias(Set.copyOf(categorias));

        // Crear imagen y obtener la URL
        try{
            String imgUrl = fileService.uploadFoto(file, "products", "Producto: " + nombre, "products", null).getImagenUrl();
            request.setImg_url(imgUrl);
        } catch (Exception e) {
            throw new RuntimeException("Error al crear la imagen del producto: " + e.getMessage());
        }

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

    @PutMapping(value = "/{id}", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ProductoResponseDto> update(@PathVariable Long id,
                                                      @RequestParam(value = "file", required = false) MultipartFile file,
                                                      @RequestParam("nombre") String nombre,
                                                      @RequestParam("marca") String marca,
                                                      @RequestParam("descripcion") String descripcion,
                                                      @RequestParam("content") String content,
                                                      @RequestParam(value = "features", required = false) String features,
                                                      @RequestParam("categorias") List<Categorias> categorias) {
        // 1. Si el campo file no está vacío, eliminar la imagen anterior, crear una nueva imagen y obtener la URL
        ProductoRequestDto request = new ProductoRequestDto();
        request.setNombre(nombre);
        request.setMarca(marca);
        request.setDescripcion(descripcion);
        request.setContent(content);
        if(features != null && !features.isEmpty()) request.setFeatures(features);
        request.setCategorias(new HashSet<>(categorias)); // Esto crea un HashSet mutable


        if (file != null && !file.isEmpty()) {
            try {
                // Primero eliminar la imagen anterior
                ProductoResponseDto existingProduct = productosService.getById(id);
                String existingImgUrl = existingProduct.getImg_url();
                fileService.deleteFotoComplete(existingImgUrl);

                // Luego crear la nueva imagen
                String imgUrl = fileService.uploadFoto(file, "products", "Producto: " + nombre, "products", null).getImagenUrl();
                request.setImg_url(imgUrl);
            } catch (Exception e) {
                throw new RuntimeException("Error al crear la imagen del producto: " + e.getMessage());
            }
        } else {
            request.setImg_url(productosService.getById(id).getImg_url());
        }

        return ResponseEntity.ok(productosService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Eliminar la imagen asociada al producto
        ProductoResponseDto existingProduct = productosService.getById(id);
        String existingImgUrl = existingProduct.getImg_url();
        try {
            fileService.deleteFotoComplete(existingImgUrl);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la imagen del producto: " + e.getMessage());
        }
        productosService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
