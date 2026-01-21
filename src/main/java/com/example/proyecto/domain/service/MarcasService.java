package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Marcas;
import com.example.proyecto.domain.entity.Productos;
import com.example.proyecto.dto.MarcasRequestDto;
import com.example.proyecto.infrastructure.MarcasRepository;
import com.example.proyecto.infrastructure.ProductosRepository;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@Service
@RequiredArgsConstructor
@Transactional
public class MarcasService {

    private final MarcasRepository marcasRepository;
    private final ProductosRepository productosRepository;

    public Marcas crear(Marcas marca) {
        // Opcional: evitar duplicados por nombre
        if (marcasRepository.existsByNombreIgnoreCase(marca.getNombre())) {
            throw new IllegalArgumentException("Ya existe una marca con el nombre: " + marca.getNombre());
        }
        marca.setId(null);
        return marcasRepository.save(marca);
    }

    public List<Marcas> listar() {
        return marcasRepository.findAll().stream()
                .map(this::toResponseWithFallbackImage)
                .toList();
    }

    public Marcas obtenerPorId(Long id) {
        return marcasRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Marca no encontrada con ID: " + id));
    }

    public Marcas actualizar(Long id, MarcasRequestDto cambios) {
        Marcas actual = obtenerPorId(id);

        actual.setNombre(cambios.getNombre());
        actual.setImagenUrl(cambios.getImagenUrl());

        return marcasRepository.save(actual);
    }

    public void eliminar(Long id) {
        if (!marcasRepository.existsById(id)) {
            throw new EntityNotFoundException("Marca no encontrada con ID: " + id);
        }
        marcasRepository.deleteById(id);
    }

    private Marcas toResponseWithFallbackImage(Marcas m) {
        String imagen = safe(m.getImagenUrl());

        // fallback: primera imagen del primer producto de esa marca
        if (imagen.isEmpty()) {
            Productos first = productosRepository
                    .findFirstByMarcaIgnoreCaseOrderByIdAsc(m.getNombre())
                    .orElse(null);

            if (first != null && first.getImg_url() != null) {
                imagen = first.getImg_url().trim();
            }
        }
        Marcas marcaConImagen = new Marcas();
        marcaConImagen.setId(m.getId());
        marcaConImagen.setNombre(m.getNombre());
        marcaConImagen.setImagenUrl(imagen);
        return marcaConImagen;
    }

    private String safe(String s) {
        return s == null ? "" : s.trim();
    }
}
