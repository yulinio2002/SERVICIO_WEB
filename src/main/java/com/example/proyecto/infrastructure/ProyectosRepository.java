package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Proyectos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface ProyectosRepository extends JpaRepository<Proyectos, Long> {
    List<Proyectos> findTop5ByOrderByIdDesc();
}
