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
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class FotosService {

    private final FotosRepository fotosRepository;
    private final ServiciosRepository serviciosRepository;
    private final ProyectosRepository proyectosRepository;

    /**
     * Nota:
     * - Fotos puede pertenecer a Servicio (1-N) o a Proyecto (1-1) pero no a ambos.
     * - Para Proyecto (1-1) existe unique constraint en fotos.proyectos_id.
     *   Antes de asignar un proyecto a una foto, se desvincula cualquier otra foto previa de ese proyecto.
     */
    @Transactional
    public Fotos create(Fotos request) {
        validateRequestForCreate(request);

        boolean hasServicio = request.getServicio() != null && request.getServicio().getId() != null;
        boolean hasProyecto = request.getProyecto() != null && request.getProyecto().getId() != null;

//        if (hasServicio == hasProyecto) {
//            throw new IllegalArgumentException("La foto debe pertenecer a un Servicio o a un Proyecto (solo uno).");
//        }

        if (hasServicio) {
            Long servicioId = request.getServicio().getId();
            validateId(servicioId);

            Servicios servicio = serviciosRepository.findById(servicioId)
                    .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + servicioId));

            request.setServicio(servicio);
            request.setProyecto(null);
        }
        else if (hasProyecto) {
            Long proyectoId = request.getProyecto().getId();
            validateId(proyectoId);

            Proyectos proyecto = proyectosRepository.findById(proyectoId)
                    .orElseThrow(() -> new ResourceNotFoundException("Proyecto no encontrado con id: " + proyectoId));

            // Evitar violación unique: fotos.proyectos_id debe ser único
            // Si ya existía una foto ligada a ese proyecto, se desvincula primero.
            List<Fotos> yaAsignadas = fotosRepository.findByProyecto_IdOrderByIdDesc(proyectoId);
            for (Fotos f : yaAsignadas) {
                f.setProyecto(null);
                fotosRepository.save(f);
            }

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
        return fotosRepository.findByServicio_IdOrderByIdDesc(servicioId);
    }

    public List<Fotos> getByProyecto(Long proyectoId) {
        validateId(proyectoId);
        return fotosRepository.findByProyecto_IdOrderByIdDesc(proyectoId);
    }

    /**
     * Reglas:
     * - Se permite cambiar imagenUrl.
     * - Se permite (opcionalmente) cambiar la pertenencia a Servicio o Proyecto.
     * - Si se asigna a Proyecto: se garantiza 1-1 desvinculando otras fotos del mismo proyecto.
     */
    @Transactional
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

            // Evitar violación unique: cualquier otra foto con ese proyecto debe quedar desvinculada.
            List<Fotos> yaAsignadas = fotosRepository.findByProyecto_IdOrderByIdDesc(proyectoId);
            for (Fotos f : yaAsignadas) {
                if (f.getId() != null && !f.getId().equals(existing.getId())) {
                    f.setProyecto(null);
                    fotosRepository.save(f);
                }
            }

            existing.setProyecto(proyecto);
            existing.setServicio(null);
        }
        // Si no manda servicio/proyecto, solo actualiza imagenUrl (mantiene relación actual)

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
        // Servicio/Proyecto se valida en create()
    }

    private void validateRequestForUpdate(Fotos request) {
        if (request == null) {
            throw new IllegalArgumentException("El body no puede ser null.");
        }
        if (isBlank(request.getImagenUrl())) {
            throw new IllegalArgumentException("La imagen (imagenUrl) es obligatoria.");
        }
        // Servicio/Proyecto son opcionales en update()
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }
}
