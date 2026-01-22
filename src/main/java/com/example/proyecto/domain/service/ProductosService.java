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
        log.info("CREATE request: {}", request);
        validateRequest(request);

        String nombre = getNombreEffective(request);

        if (productosRepository.existsByNombreIgnoreCase(nombre)) {
            throw new ConflictException("Ya existe un producto con el nombre: " + nombre);
        }

        Productos p = new Productos();
        p.setId(null); // importante en create
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

        String nombre = getNombreEffective(request);

        if (productosRepository.existsByNombreIgnoreCaseAndIdNot(nombre, id)) {
            throw new ConflictException("Ya existe otro producto con el nombre: " + nombre);
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
        p.setNombre(getNombreEffective(request));
        p.setDescripcion(getDescripcionEffective(request));
        p.setContent(getContentEffective(request));
        p.setMarca(getMarcaEffective(request));

        // Categorías: siempre viene del front, si no viene lanzamos error en validate
        Set<Categorias> cats = request.getCategorias();
        p.setCategorias(cats);

        // img_url: puede venir directo o desde images/galleryImages
        p.setImg_url(getImgUrlEffective(request));

        // features: lista o string
        String featuresRaw = normalizeFeaturesRaw(request);
        p.setFeatures(featuresRaw);
    }

    private String getNombreEffective(ProductoRequestDto request) {
        if (!isBlank(request.getNombre())) return request.getNombre().trim();
        if (!isBlank(request.getTitle())) return request.getTitle().trim();
        return null;
    }

    private String getDescripcionEffective(ProductoRequestDto request) {
        if (!isBlank(request.getDescripcion())) return request.getDescripcion().trim();
        if (!isBlank(request.getDescription())) return request.getDescription().trim();
        return null;
    }

    private String getContentEffective(ProductoRequestDto request) {
        if (!isBlank(request.getContent())) return request.getContent().trim();
        return null;
    }

    private String getMarcaEffective(ProductoRequestDto request) {
        if (!isBlank(request.getMarca())) return request.getMarca().trim();
        return null;
    }

    private String getImgUrlEffective(ProductoRequestDto request) {
        // 1) si viene img_url
        if (!isBlank(request.getImg_url())) return request.getImg_url().trim();

        return null;
    }

    private String normalizeFeaturesRaw(ProductoRequestDto request) {
        // Prioridad: featuresList (array)
        if (request.getFeaturesList() != null) {
            return encodeFromList(List.of(request.getFeaturesList()));
        }

        // Si viene string features (a;b;c)
        if (!isBlank(request.getFeatures())) {
            return encodeFromList(decodeToList(request.getFeatures()));
        }

        // Si el front manda features como lista pero tú no la modelaste como List<String>,
        // entonces debe mapear a featuresList. (En front, envía featuresList o features "a;b;c")
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

        String nombre = getNombreEffective(request);
        String img = getImgUrlEffective(request);
        String desc = getDescripcionEffective(request);
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
