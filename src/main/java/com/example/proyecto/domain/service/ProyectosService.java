package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Proyectos;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.FotosRepository;
import com.example.proyecto.infrastructure.ProyectosRepository;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
@Service
@RequiredArgsConstructor
@Transactional
public class ProyectosService {

    private final ProyectosRepository proyectosRepository;
    private final FotosRepository fotosRepository;

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
        existing.setDescripcion(request.getDescripcion());

        return proyectosRepository.save(existing);
    }

    /**
     * Evita violación de FK:
     * Fotos tiene FK (unique) hacia Proyectos en la relación 1-1 (fotos.proyectos_id).
     * Se borra primero la foto asociada y luego el proyecto.
     */
    public void delete(Long id) {
        validateId(id);

        if (!proyectosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Proyecto no encontrado con id: " + id);
        }

        // 1) borrar dependiente (si existe)
        fotosRepository.deleteByProyecto_Id(id);

        // 2) borrar el padre
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
        if (isBlank(request.getDescripcion())) {
            throw new IllegalArgumentException("La descripción es obligatoria.");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
