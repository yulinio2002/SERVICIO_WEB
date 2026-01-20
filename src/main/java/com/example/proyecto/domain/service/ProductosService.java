package com.example.proyecto.domain.service;

import com.example.proyecto.dto.ProductoRequestDto;
import com.example.proyecto.dto.ProductoResponseDto;
import com.example.proyecto.domain.entity.Productos;
import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.exception.ConflictException;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ProductosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class ProductosService {

    private final ProductosRepository productosRepository;

    public ProductoResponseDto create(ProductoRequestDto request) {
        validateRequest(request);

        if (productosRepository.existsByNombreIgnoreCase(request.getNombre())) {
            throw new ConflictException("Ya existe un producto con el nombre: " + request.getNombre());
        }

        Productos p = new Productos();
        applyRequestToEntity(p, request);

        Productos saved = productosRepository.save(p);
        return toResponse(saved);
    }

    public ProductoResponseDto getById(Long id) {
        Productos p = productosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));
        return toResponse(p);
    }

    public List<ProductoResponseDto> list(Categorias categoria, String marca) {
        if (categoria != null && marca != null && !marca.trim().isEmpty()) {
            throw new IllegalArgumentException("No puedes filtrar por categoria y marca al mismo tiempo.");
        }

        List<Productos> list;
        if (categoria != null) {
            list = productosRepository.findByCategoriasOrderByNombreAsc(categoria);
        } else if (marca != null && !marca.trim().isEmpty()) {
            list = productosRepository.findByMarcaIgnoreCaseOrderByNombreAsc(marca.trim());
        } else {
            list = productosRepository.findAll();
        }

        return list.stream().map(this::toResponse).collect(Collectors.toList());
    }

    public ProductoResponseDto update(Long id, ProductoRequestDto request) {
        validateRequest(request);

        Productos existing = productosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        if (productosRepository.existsByNombreIgnoreCaseAndIdNot(request.getNombre(), id)) {
            throw new ConflictException("Ya existe otro producto con el nombre: " + request.getNombre());
        }

        applyRequestToEntity(existing, request);
        Productos saved = productosRepository.save(existing);
        return toResponse(saved);
    }

    public void delete(Long id) {
        if (!productosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }
        productosRepository.deleteById(id);
    }

    private void applyRequestToEntity(Productos p, ProductoRequestDto request) {
        p.setNombre(request.getNombre());
        p.setImg_url(request.getImg_url());
        p.setDescripcion(request.getDescripcion());
        p.setContent(request.getContent());
        p.setMarca(request.getMarca());
        p.setCategorias(request.getCategorias());

        // ✅ features: acepta lista o string
        String featuresRaw = normalizeFeaturesRaw(request);
        p.setFeatures(featuresRaw);
    }

    private String normalizeFeaturesRaw(ProductoRequestDto request) {
        // Si viene featuresList, tiene prioridad
        if (request.getFeaturesList() != null) {
            return encodeFromList(List.of(request.getFeaturesList()));
        }
        // Si viene string, normalizamos
        String raw = request.getFeatures();
        if (raw == null) return "";
        return encodeFromList(decodeToList(raw));
    }

    private ProductoResponseDto toResponse(Productos p) {
        return ProductoResponseDto.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .img_url(p.getImg_url())
                .descripcion(p.getDescripcion())
                .content(p.getContent())
                .features(decodeToList(p.getFeatures())) //  lista
                .marca(p.getMarca())
                .categorias(p.getCategorias())
                .build();
    }

    private void validateRequest(ProductoRequestDto request) {
        if (request == null) throw new IllegalArgumentException("El body no puede ser null.");
        if (isBlank(request.getNombre())) throw new IllegalArgumentException("El nombre es obligatorio.");
        if (isBlank(request.getImg_url())) throw new IllegalArgumentException("La imagen (img_url) es obligatoria.");
        if (isBlank(request.getDescripcion())) throw new IllegalArgumentException("La descripción es obligatoria.");
        if (isBlank(request.getContent())) throw new IllegalArgumentException("El content es obligatorio.");
        if (isBlank(request.getMarca())) throw new IllegalArgumentException("La marca es obligatoria.");
        if (request.getCategorias() == null) throw new IllegalArgumentException("Las categorías no pueden ser null.");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    public static List<String> decodeToList(String raw) {
        if (raw == null || raw.trim().isEmpty()) return new ArrayList<>();

        return Arrays.stream(raw.split(";"))
                .map(String::trim)
                .filter(s -> !s.isEmpty())
                .collect(Collectors.toList());
    }

    public static String encodeFromList(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        return list.stream()
                .map(s -> s == null ? "" : s.trim())
                .filter(s -> !s.isEmpty())
                .collect(Collectors.joining(";"));
    }
}
