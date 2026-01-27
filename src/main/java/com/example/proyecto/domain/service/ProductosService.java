package com.example.proyecto.domain.service;

import com.example.proyecto.dto.ProductoRequestDto;
import com.example.proyecto.dto.ProductoResponseDto;
import com.example.proyecto.domain.entity.Productos;
import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.exception.ConflictException;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ProductosRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductosService {

    private final ProductosRepository productosRepository;

    @Transactional
    public ProductoResponseDto create(ProductoRequestDto request) {
        // Mostrar en consola el dato que llega en request
        //log.info("CREATE request: {}", request);
        validateRequest(request);

        String nombre = request.getNombre().trim();

        if (productosRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictException("Ya existe un producto con el nombre: " + nombre);
        }

        Productos p = new Productos();
        p.setId(null);
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

    @Transactional
    public ProductoResponseDto update(Long id, ProductoRequestDto request) {
        validateRequest(request);

        Productos existing = productosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Producto no encontrado con id: " + id));

        String nombre = request.getNombre().trim();

        applyRequestToEntity(existing, request);
        Productos saved = productosRepository.save(existing);
        return toResponse(saved);
    }

    @Transactional
    public void delete(Long id) {
        if (!productosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Producto no encontrado con id: " + id);
        }
        productosRepository.deleteById(id);
    }

    private void applyRequestToEntity(Productos p, ProductoRequestDto request) {
        p.setNombre(request.getNombre().trim());
        p.setDescripcion(request.getDescripcion().trim());
        p.setContent(getContentEffective(request));
        p.setMarca(getMarcaEffective(request));

        // Categorías: siempre viene del front, si no viene lanzamos error en validate
        Set<Categorias> cats = request.getCategorias();
        p.setCategorias(cats);

        // img_url: puede venir directo o desde images/galleryImages
        p.setImg_url(request.getImg_url().trim());

        // features: string
        String featuresRaw = normalizeFeaturesRaw(request);
        p.setFeatures(featuresRaw);
    }


    private String getContentEffective(ProductoRequestDto request) {
        if (!isBlank(request.getContent())) return request.getContent().trim();
        return null;
    }

    private String getMarcaEffective(ProductoRequestDto request) {
        if (!isBlank(request.getMarca())) return request.getMarca().trim();
        return null;
    }


    private String normalizeFeaturesRaw(ProductoRequestDto request) {

        // Si viene string features (a;b;c)
        if (!isBlank(request.getFeatures())) {
            return encodeFromList(decodeToList(request.getFeatures()));
        }

        return "";
    }

    private ProductoResponseDto toResponse(Productos p) {
        return ProductoResponseDto.builder()
                .id(p.getId())
                .nombre(p.getNombre())
                .img_url(p.getImg_url())
                .descripcion(p.getDescripcion())
                .content(p.getContent())
                .features(decodeToList(p.getFeatures()))
                .marca(p.getMarca())
                .categorias(p.getCategorias())
                .build();
    }

    private void validateRequest(ProductoRequestDto request) {
        if (request == null) throw new IllegalArgumentException("El body no puede ser null.");

        String nombre = request.getNombre().trim();
        String img = request.getImg_url().trim();
        String desc = request.getDescripcion().trim();
        String content = getContentEffective(request);
        String marca = getMarcaEffective(request);

        if (isBlank(nombre)) throw new IllegalArgumentException("El nombre/title es obligatorio.");
        if (isBlank(img)) throw new IllegalArgumentException("La imagen es obligatoria (img_url o images[0] o galleryImages[0].url).");
        if (isBlank(desc)) throw new IllegalArgumentException("La descripción es obligatoria (descripcion/description).");
        if (isBlank(content)) throw new IllegalArgumentException("El content es obligatorio.");
        if (isBlank(marca)) throw new IllegalArgumentException("La marca es obligatoria.");
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
