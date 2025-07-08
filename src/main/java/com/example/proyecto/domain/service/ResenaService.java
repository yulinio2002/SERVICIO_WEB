package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Cliente;
import com.example.proyecto.domain.entity.Proveedor;
import com.example.proyecto.domain.entity.Resena;
import com.example.proyecto.domain.entity.Servicio;
import com.example.proyecto.dto.ResenaDTO;
import com.example.proyecto.dto.ResenaRequestDto;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.infrastructure.ClienteRepository;
import com.example.proyecto.infrastructure.ProveedorRepository;
import com.example.proyecto.infrastructure.ResenaRepository;
import com.example.proyecto.infrastructure.ServicioRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class ResenaService {

    private final ResenaRepository resenaRepository;
    private final ServicioRepository servicioRepository;
    private final ClienteRepository clienteRepository;
    private final ModelMapper modelMapper;
    private final ProveedorRepository proveedorRepository;

    public ResenaDTO crearResena(ResenaRequestDto dto) {
        Servicio servicio = servicioRepository.findById(dto.getServicioId())
                .orElseThrow(() -> new ResourceNotFoundException("Servicio no encontrado: " + dto.getServicioId()));
        Cliente cliente = clienteRepository.findById(dto.getClienteId())
                .orElseThrow(() -> new ResourceNotFoundException("Cliente no encontrado: " + dto.getClienteId()));
        Resena resena = new Resena();
        resena.setCliente(cliente);
        resena.setServicio(servicio);
        resena.setCalificacion(dto.getCalificacion());
        resena.setComentario(dto.getComentario());
        resena.setFecha(dto.getFecha());

        // 3) Guardar y devolver DTO de respuesta
        Resena guardada = resenaRepository.save(resena);
        // Actualiza rating del proveedor
        actualizarRatingProveedor(resena.getServicio().getProveedor());
        return modelMapper.map(guardada, ResenaDTO.class);
    }

    public List<ResenaDTO> obtenerResenasPorServicio(Long servicioId) {
        List<Resena> resenas = resenaRepository.findByServicioId(servicioId);
        return resenas.stream()
                .map(r -> modelMapper.map(r, ResenaDTO.class))
                .collect(Collectors.toList());
    }

    private void actualizarRatingProveedor(Proveedor proveedor) {
        // Obtener todos los servicios del proveedor
        List<Servicio> servicios = proveedor.getServicios();

        // Obtener todas las reseñas de todos los servicios del proveedor
        List<Resena> todasLasResenas = servicios.stream()
                .flatMap(servicio -> servicio.getResenas().stream())
                .collect(Collectors.toList());

        // Calcular el nuevo rating (promedio de todas las calificaciones)
        if (!todasLasResenas.isEmpty()) {
            double promedio = todasLasResenas.stream()
                    .mapToInt(Resena::getCalificacion)
                    .average()
                    .orElse(0.0);

            proveedor.setRating(BigDecimal.valueOf(promedio));
        } else {
            proveedor.setRating(BigDecimal.ZERO);
        }

        // Guardar el proveedor con el nuevo rating
        proveedorRepository.save(proveedor);
    }
}