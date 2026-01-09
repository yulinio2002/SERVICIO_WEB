package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Marcas;
import com.example.proyecto.infrastructure.MarcasRepository;
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

    public Marcas crear(Marcas marca) {
        // Opcional: evitar duplicados por nombre
        if (marcasRepository.existsByNombreIgnoreCase(marca.getNombre())) {
            throw new IllegalArgumentException("Ya existe una marca con el nombre: " + marca.getNombre());
        }
        return marcasRepository.save(marca);
    }

    public List<Marcas> listar() {
        return marcasRepository.findAll();
    }

    public Marcas obtenerPorId(Long id) {
        return marcasRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Marca no encontrada con ID: " + id));
    }

    public Marcas actualizar(Long id, Marcas cambios) {
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
}
