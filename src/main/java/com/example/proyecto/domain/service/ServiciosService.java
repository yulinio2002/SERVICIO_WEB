package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Fotos;
import com.example.proyecto.domain.entity.Servicios;
import com.example.proyecto.dto.ServicioDTO;
import com.example.proyecto.dto.ServicioUpdateRequestDTO;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.FotosRepository;
import com.example.proyecto.infrastructure.ServiciosRepository;
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
public class ServiciosService {

    private final ServiciosRepository serviciosRepository;
    private final FotosRepository fotosRepository;

    @Transactional
    public Servicios create(ServicioUpdateRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("El body no puede ser null.");
        if (isBlank(request.getNombre())) throw new IllegalArgumentException("title es obligatorio.");
        if (isBlank(request.getDescripcion())) throw new IllegalArgumentException("description es obligatorio.");
        if (isBlank(request.getContent())) throw new IllegalArgumentException("content es obligatorio.");
        if (request.getFeatures() == null) throw new IllegalArgumentException("features es obligatorio (puede ser lista vacía).");


        // Mapeo FE -> BE
        Servicios newServicio = new Servicios();
        newServicio.setId(null);
        newServicio.setNombre(request.getNombre());
        newServicio.setDescripcion(request.getDescripcion());
        newServicio.setContent(request.getContent());
        newServicio.setFeatures(request.getFeatures());

        Servicios saved = serviciosRepository.save(newServicio);

        return saved;
    }

    public Servicios getById(Long id) {
        validateId(id);
        return serviciosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + id));
    }

    public List<ServicioDTO> getAll() {
        return serviciosRepository.findAll().stream()
                .map(this::mapToServicioDTO)
                .toList();
    }

    public List<ServicioDTO> top5ByIdDesc() {
        return serviciosRepository.findTop5ByOrderByIdDesc().stream()
                .map(this::mapToServicioDTO)
                .toList();
    }

    @Transactional
    public Servicios update(Long id, ServicioUpdateRequestDTO request) {
        if (id == null || id <= 0) throw new IllegalArgumentException("El id debe ser positivo.");
        if (request == null) throw new IllegalArgumentException("El body no puede ser null.");
        if (isBlank(request.getNombre())) throw new IllegalArgumentException("title es obligatorio.");
        if (isBlank(request.getDescripcion())) throw new IllegalArgumentException("description es obligatorio.");
        if (isBlank(request.getContent())) throw new IllegalArgumentException("content es obligatorio.");
        if (request.getFeatures() == null) throw new IllegalArgumentException("features es obligatorio (puede ser lista vacía).");

        Servicios existing = serviciosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + id));

        // Mapeo FE -> BE
        existing.setNombre(request.getNombre());
        existing.setDescripcion(request.getDescripcion());
        existing.setContent(request.getContent());
        existing.setFeatures(request.getFeatures());

        return serviciosRepository.save(existing);
    }

    /**
     * Importante:
     * Si existen Fotos con FK hacia Servicios, borrar primero las fotos evita violaciones de FK.
     */
    @Transactional
    public void delete(Long id) {
        validateId(id);

        if (!serviciosRepository.existsById(id)) {
            throw new ResourceNotFoundException("Servicio no encontrado con id: " + id);
        }

        // 1) borrar dependientes
        fotosRepository.deleteByServicio_Id(id);

        // 2) borrar el padre
        serviciosRepository.deleteById(id);
    }

    private void validateId(Long id) {
        if (id == null || id <= 0) {
            throw new IllegalArgumentException("El id debe ser un número positivo.");
        }
    }


    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

    private ServicioDTO mapToServicioDTO(Servicios s) {
        ServicioDTO ns = new ServicioDTO();
        ns.setId(s.getId());
        ns.setTitle(s.getNombre());
        ns.setImage((s.getFotos() == null || s.getFotos().isEmpty()) ? " " : s.getFotos().get(0).getImagenUrl());
        return ns;
    }

}
