package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Fotos;
import com.example.proyecto.domain.entity.Proyectos;
import com.example.proyecto.domain.entity.Servicios;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.FotosRepository;
import com.example.proyecto.infrastructure.ProyectosRepository;
import com.example.proyecto.infrastructure.ServiciosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FotosService {

    private final FotosRepository fotosRepository;
    private final ServiciosRepository serviciosRepository;
    private final ProyectosRepository proyectosRepository;

    public Fotos create(Fotos request) {
        validateRequestForCreate(request);

        boolean hasServicio = request.getServicio() != null && request.getServicio().getId() != null;
        boolean hasProyecto = request.getProyecto() != null && request.getProyecto().getId() != null;

        if (hasServicio == hasProyecto) {
            throw new IllegalArgumentException("La foto debe pertenecer a un Servicio o a un Proyecto (solo uno).");
        }

        if (hasServicio) {
            Long servicioId = request.getServicio().getId();
            validateId(servicioId);

            Servicios servicio = serviciosRepository.findById(servicioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + servicioId));

            request.setServicio(servicio);
            request.setProyecto(null);
        } else {
            Long proyectoId = request.getProyecto().getId();
            validateId(proyectoId);

            Proyectos proyecto = proyectosRepository.findById(proyectoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + proyectoId));

            request.setProyecto(proyecto);
            request.setServicio(null);
        }

        return fotosRepository.save(request);
    }

    public Fotos getById(Long id) {
        validateId(id);
        return fotosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Foto no encontrada con id: " + id));
    }

    public List<Fotos> getAll() {
        return fotosRepository.findAll();
    }

    public List<Fotos> getByServicio(Long servicioId) {
        validateId(servicioId);

        // Validar que exista el servicio (opcional pero recomendado)
        if (!serviciosRepository.existsById(servicioId)) {
            throw new ResourceNotFoundException("Servicio no encontrado con id: " + servicioId);
        }

        return fotosRepository.findByServicio_IdOrderByIdDesc(servicioId);
    }

    public List<Fotos> getByProyecto(Long proyectoId) {
        validateId(proyectoId);

        if (!proyectosRepository.existsById(proyectoId)) {
            throw new ResourceNotFoundException("Proyecto no encontrado con id: " + proyectoId);
        }

        return fotosRepository.findByProyecto_IdOrderByIdDesc(proyectoId);
    }

    public Fotos update(Long id, Fotos request) {
        validateId(id);
        validateRequestForUpdate(request);

        Fotos existing = fotosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Foto no encontrada con id: " + id));

        existing.setImagenUrl(request.getImagenUrl());

        boolean wantsServicio = request.getServicio() != null && request.getServicio().getId() != null;
        boolean wantsProyecto = request.getProyecto() != null && request.getProyecto().getId() != null;

        if (wantsServicio && wantsProyecto) {
            throw new IllegalArgumentException("No puedes asignar Servicio y Proyecto al mismo tiempo.");
        }

        if (wantsServicio) {
            Long servicioId = request.getServicio().getId();
            validateId(servicioId);

            Servicios servicio = serviciosRepository.findById(servicioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + servicioId));

            existing.setServicio(servicio);
            existing.setProyecto(null);
        } else if (wantsProyecto) {
            Long proyectoId = request.getProyecto().getId();
            validateId(proyectoId);

            Proyectos proyecto = proyectosRepository.findById(proyectoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + proyectoId));

            existing.setProyecto(proyecto);
            existing.setServicio(null);
        }

        return fotosRepository.save(existing);
    }

    public void delete(Long id) {
        validateId(id);

        if (!fotosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Foto no encontrada con id: " + id);
        }

        fotosRepository.deleteById(id);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id debe ser un número positivo.");
        }
    }

    private void validateRequestForCreate(Fotos request) {
        if (request == null) {
            throw new IllegalArgumentException("El body no puede ser null.");
        }
        if (isBlank(request.getImagenUrl())) {
            throw new IllegalArgumentException("La imagen (imagenUrl) es obligatoria.");
        }
    }

    private void validateRequestForUpdate(Fotos request) {
        if (request == null) {
            throw new IllegalArgumentException("El body no puede ser null.");
        }
        if (isBlank(request.getImagenUrl())) {
            throw new IllegalArgumentException("La imagen (imagenUrl) es obligatoria.");
        }
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
