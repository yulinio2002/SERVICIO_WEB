package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Servicios;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ServiciosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ServiciosService {

    private final ServiciosRepository serviciosRepository;

    public Servicios create(Servicios request) {
        validateRequest(request);
        return serviciosRepository.save(request);
    }

    public Servicios getById(Long id) {
        validateId(id);
        return serviciosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + id));
    }

    public List<Servicios> getAll() {
        return serviciosRepository.findAll();
    }

    public Servicios update(Long id, Servicios request) {
        validateId(id);
        validateRequest(request);

        Servicios existing = serviciosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + id));

        existing.setNombre(request.getNombre());
        existing.setImagenUrl(request.getImagenUrl());
        existing.setDescripcion(request.getDescripcion());

        // Recomendación: manejar fotos con endpoints/servicio propio, no en el update del servicio.
        return serviciosRepository.save(existing);
    }

    public void delete(Long id) {
        validateId(id);

        if (!serviciosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Servicio no encontrado con id: " + id);
        }

        serviciosRepository.deleteById(id);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id debe ser un número positivo.");
        }
    }

    private void validateRequest(Servicios request) {
        if (request == null) {
            throw new IllegalArgumentException("El body no puede ser null.");
        }
        if (isBlank(request.getNombre())) {
            throw new IllegalArgumentException("El nombre es obligatorio.");
        }
        if (isBlank(request.getImagenUrl())) {
            throw new IllegalArgumentException("La imagen (imagenUrl) es obligatoria.");
        }
        if (isBlank(request.getDescripcion())) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
