package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Proyectos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface ProyectosRepository extends JpaRepository<Proyectos, Long> {
    List<Proyectos> findTop5ByOrderByIdDesc();
}
