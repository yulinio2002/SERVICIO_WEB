package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Proyectos;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ProyectosRepository;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@Service
@RequiredArgsConstructor
public class ProyectosService {

    private final ProyectosRepository proyectosRepository;

    public Proyectos create(Proyectos request) {
        validateRequest(request);
        return proyectosRepository.save(request);
    }

    public Proyectos getById(Long id) {
        validateId(id);
        return proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + id));
    }

    public List<Proyectos> getAll() {
        return proyectosRepository.findAll();
    }

    public List<Proyectos> top5ByIdDesc() {
        return proyectosRepository.findTop5ByOrderByIdDesc();
    }

    public Proyectos update(Long id, Proyectos request) {
        validateId(id);
        validateRequest(request);

        Proyectos existing = proyectosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + id));

        existing.setNombre(request.getNombre());
        existing.setImg_url(request.getImg_url());
        existing.setDescripcion(request.getDescripcion());

        return proyectosRepository.save(existing);
    }

    public void delete(Long id) {
        validateId(id);

        if (!proyectosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proyecto no encontrado con id: " + id);
        }

        proyectosRepository.deleteById(id);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id debe ser un número positivo.");
        }
    }

    private void validateRequest(Proyectos request) {
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
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
