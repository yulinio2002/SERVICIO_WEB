package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Fotos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface FotosRepository extends JpaRepository <Fotos, Long> {
    List<Fotos> findByServicio_IdOrderByIdDesc(Long servicioId);

    List<Fotos> findByProyectos_IdOrderByIdDesc(Long proyectoId);
}
