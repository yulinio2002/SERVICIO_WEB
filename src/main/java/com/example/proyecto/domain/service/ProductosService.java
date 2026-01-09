package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Productos;
import com.example.proyecto.exception.ConflictException;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ProductosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ProductosService {
    private final ProductosRepository productosRepository;

    public Productos create(Productos request) {
        validateRequest(request);

        if (productosRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ConflictException("Ya existe un producto con el nombre: " + request.getNombre());
        }

        return productosRepository.save(request);
    }

    public Productos getById(Long id) {
        validateId(id);
        return productosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
    }

    public List<Productos> getAll() {
        return productosRepository.findAll();
    }

    public Productos update(Long id, Productos request) {
        validateId(id);
        validateRequest(request);

        Productos existing = productosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        if (productosRepository.existsByNombreIgnoreCaseAndIdNot(request.getNombre(), id)) {
            throw new ConflictException("Ya existe otro producto con el nombre: " + request.getNombre());
        }

        existing.setNombre(request.getNombre());
        existing.setImg_url(request.getImg_url());
        existing.setDescripcion(request.getDescripcion());
        existing.setMarca(request.getMarca());
        existing.setCategorias(request.getCategorias());

        return productosRepository.save(existing);
    }

    public void delete(Long id) {
        validateId(id);

        if (!productosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }

        productosRepository.deleteById(id);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id debe ser un número positivo.");
        }
    }

    private void validateRequest(Productos request) {
        if (request == null) {
            throw new IllegalArgumentException("El body no puede ser null.");
        }
        if (isBlank(request.getNombre())) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (isBlank(request.getImg_url())) {
            throw new IllegalArgumentException("La imagen (img_url) es obligatoria.");
        }
        if (isBlank(request.getDescripcion())) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }
        if (isBlank(request.getMarca())) {
            throw new IllegalArgumentException("La marca es obligatoria.");
        }
        // categorias puede ser vacía, pero no debería ser null si quieres evitar NPE:
        if (request.getCategorias() == null) {
            throw new IllegalArgumentException("Las categorías no pueden ser null.");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
