package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Proyectos;
import com.example.proyecto.domain.entity.Servicio;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProyectosRepository extends JpaRepository<Proyectos, Long> {
}
