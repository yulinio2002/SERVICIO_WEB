package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.*;
import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.dto.ServicioRequestDto;
import com.example.proyecto.dto.FiltroServicioDTO;
import com.example.proyecto.dto.ServicioDTO;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ServicioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.data.jpa.domain.Specification;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import jakarta.persistence.criteria.Predicate;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class ServicioService {

    private final ServicioRepository servicioRepository;
    private final ModelMapper modelMapper;

    public Servicios findById(Long id) {
        return servicioRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Servicio "+ id));
    }
    public void actualizarServicio(Long servicioId, ServicioRequestDto dto) {
        Servicios existing = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado: " + servicioId));
        existing.setNombre(dto.getNombre());
        existing.setDescripcion(dto.getDescripcion());
        existing.setPrecio(dto.getPrecio());
        Categorias categoriaDto = Categorias.valueOf(dto.getCategoria());
        existing.setCategoria(categoriaDto);
        servicioRepository.save(existing);
    }

    public List<ServicioDTO> buscarServicios(FiltroServicioDTO filtros) {
        // 1. Construir Specification dinámico
        Specification<Servicios> spec = (root, query, cb) -> {
            List<jakarta.persistence.criteria.Predicate> preds = new ArrayList<>();

            // Solo servicios activos
            preds.add(cb.equal(root.get("activo"), true));

            if (filtros.getCategoria() != null ) {
                try {
                    preds.add(cb.equal(
                            root.get("categoria"),
                            filtros.getCategoria()
                    ));
                } catch (IllegalArgumentException e) {
                    // Si la categoría no es válida, ignorar este filtro
                    // o podrías lanzar una excepción personalizada
                }
            }

            // NOTA: Removido filtro por dirección ya que Servicio no tiene ese campo
            // Si necesitas filtrar por dirección, deberías hacerlo por la dirección del proveedor
            // o agregar un campo dirección a la entidad Servicio

            if (filtros.getPrecioMin() != null) {
                preds.add(cb.greaterThanOrEqualTo(
                        root.get("precio"),
                        BigDecimal.valueOf(filtros.getPrecioMin())
                ));
            }

            if (filtros.getPrecioMax() != null) {
                preds.add(cb.lessThanOrEqualTo(
                        root.get("precio"),
                        BigDecimal.valueOf(filtros.getPrecioMax())
                ));
            }

            // NOTA: Removido filtro por calificación ya que Servicio no tiene rating propio
            // El rating está en el Proveedor, no en el Servicio
            // Si necesitas filtrar por calificación, sería:
            // preds.add(cb.greaterThanOrEqualTo(root.get("proveedor").get("rating"), filtros.getCalificacionMin()));

            if (filtros.getCalificacionMin() != null) {
                preds.add(cb.greaterThanOrEqualTo(
                        root.get("proveedor").get("rating"),
                        BigDecimal.valueOf(filtros.getCalificacionMin())
                ));
            }

            return cb.and(preds.toArray(new Predicate[0]));
        };

        // 2. Preparar paginación
        Pageable pageable = PageRequest.of(
                filtros.getPage(),
                filtros.getSize(),
                Sort.by("id").ascending()
        );

        // 3. Ejecutar consulta paginada
        Page<Servicios> page = servicioRepository.findAll(spec, pageable);

        // 4. Mapear a DTO y devolver la lista
        return page.getContent().stream()
                .map(srv -> toDTO(srv))
                .collect(Collectors.toList());
    }

    //Listas servicios activos
    public List<ServicioDTO> listarServiciosActivos() {
        return servicioRepository.findByActivoTrue()
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    public List<ServicioDTO> listarServiciosPorProveedor(Long proveedorId) {
        return servicioRepository.findByProveedorId(proveedorId)
                .stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }

    //Cambiar estado de un servicio
    public ServicioDTO cambiarEstado(Long servicioId, boolean activo) {
        Servicios srv = servicioRepository.findById(servicioId)
                .orElseThrow(() -> new IllegalArgumentException("Servicio no encontrado"));
        srv.setActivo(activo);
        return toDTO(servicioRepository.save(srv));
    }

    public boolean eliminarServicio(Long servicioId) {
        if (servicioRepository.existsById(servicioId)) {
            servicioRepository.deleteById(servicioId);
            return true;
        }
        return false;
    }

    public List<Categorias> listarCategorias() {
        return Categorias.listarCategorias();
    }
   private ServicioDTO toDTO(Servicios s) {
       ServicioDTO dto = new ServicioDTO();
       dto.setId(s.getId());
       dto.setNombre(s.getNombre());
       dto.setDescripcion(s.getDescripcion());
       dto.setPrecio(s.getPrecio());
       dto.setActivo(s.isActivo());
       dto.setCategoria(s.getCategoria());
       dto.setProveedorId(s.getProveedor().getId());
       return dto;
   }
}
