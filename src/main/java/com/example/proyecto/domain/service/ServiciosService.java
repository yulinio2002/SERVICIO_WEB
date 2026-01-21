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

    @Transactional
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
    public ServicioDTO create(ServicioUpdateRequestDTO request) {
        if (request == null) throw new IllegalArgumentException("El body no puede ser null.");
        if (isBlank(request.getTitle())) throw new IllegalArgumentException("title es obligatorio.");
        if (isBlank(request.getDescription())) throw new IllegalArgumentException("description es obligatorio.");
        if (isBlank(request.getContent())) throw new IllegalArgumentException("content es obligatorio.");
        if (request.getFeatures() == null) throw new IllegalArgumentException("features es obligatorio (puede ser lista vacía).");

        // imagenUrl (hero)
        String hero = null;
        if (request.getImages() != null && !request.getImages().isEmpty() && !isBlank(request.getImages().get(0))) {
            hero = request.getImages().get(0);
        } else if (request.getGalleryImages() != null && !request.getGalleryImages().isEmpty()
                && !isBlank(request.getGalleryImages().get(0).getUrl())) {
            hero = request.getGalleryImages().get(0).getUrl();
        }
        if (isBlank(hero)) {
            throw new IllegalArgumentException("images[0] o galleryImages[0].url es obligatorio para imagenUrl.");
        }

        // Mapeo FE -> BE
        Servicios newServicio = new Servicios();
        newServicio.setId(null); // IMPORTANTE: antes de guardar
        newServicio.setNombre(request.getTitle());
        newServicio.setDescripcion(request.getDescription());
        newServicio.setContent(request.getContent());
        newServicio.setFeatures(request.getFeatures());
        newServicio.setImagenUrl(hero);

        // 1) Guardar primero para obtener ID real
        Servicios saved = serviciosRepository.save(newServicio);

        // 2) Ahora sí sincronizar fotos usando el ID real
        syncServicePhotosReplaceAll(saved.getId(), request);

        // (Opcional pero útil) si quieres que el DTO refleje el hero sin depender del lazy fotos:
        return mapToServicioDTO(saved);
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
        if (isBlank(request.getTitle())) throw new IllegalArgumentException("title es obligatorio.");
        if (isBlank(request.getDescription())) throw new IllegalArgumentException("description es obligatorio.");
        if (isBlank(request.getContent())) throw new IllegalArgumentException("content es obligatorio.");
        if (request.getFeatures() == null) throw new IllegalArgumentException("features es obligatorio (puede ser lista vacía).");

        Servicios existing = serviciosRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado con id: " + id));

        // Mapeo FE -> BE
        existing.setNombre(request.getTitle());
        existing.setDescripcion(request.getDescription());
        existing.setContent(request.getContent());
        existing.setFeatures(request.getFeatures());

        // imagenUrl: tomar de images[0] o galleryImages[0].url si viene
        String hero = null;
        if (request.getImages() != null && !request.getImages().isEmpty() && !isBlank(request.getImages().get(0))) {
            hero = request.getImages().get(0);
        } else if (request.getGalleryImages() != null && !request.getGalleryImages().isEmpty()
                && !isBlank(request.getGalleryImages().get(0).getUrl())) {
            hero = request.getGalleryImages().get(0).getUrl();
        }
        if (isBlank(hero)) {
            // Si tu modelo exige imagenUrl NOT NULL, debes garantizarlo
            // Puedes mantener el existente si no viene hero:
            // hero = existing.getImagenUrl();
            throw new IllegalArgumentException("images[0] o galleryImages[0].url es obligatorio para imagenUrl.");
        }
        existing.setImagenUrl(hero);

        syncServicePhotosReplaceAll(existing.getId(), request);

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

    private ServicioDTO mapToServicioDTO(Servicios s) {
        ServicioDTO ns = new ServicioDTO();
        ns.setId(s.getId());
        ns.setTitle(s.getNombre());
        ns.setImage((s.getFotos() == null || s.getFotos().isEmpty()) ? " " : s.getFotos().get(0).getImagenUrl());
        return ns;
    }


    private void syncServicePhotosReplaceAll(Long servicioId, ServicioUpdateRequestDTO request) {
        // galleryImages puede venir null o vacía
        if (request.getGalleryImages() == null) {
            return; // si quieres que null signifique "no tocar", deja así
        }

        // Borrado total de dependientes
        fotosRepository.deleteByServicio_Id(servicioId);

        // Insertar nuevas
        for (ServicioUpdateRequestDTO.GalleryImage gi : request.getGalleryImages()) {
            if (gi == null || isBlank(gi.getUrl())) continue;

            Fotos f = new Fotos();
            f.setImagenUrl(gi.getUrl());
            f.setAlt(request.getTitle().toLowerCase());
            f.setServicio(serviciosRepository.getReferenceById(servicioId)); // evita query extra
            f.setProyecto(null);

            fotosRepository.save(f);
        }
    }
}
