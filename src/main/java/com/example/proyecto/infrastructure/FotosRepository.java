package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Fotos;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface FotosRepository extends JpaRepository <Fotos, Long> {
    List<Fotos> findByServicio_IdOrderByIdDesc(Long servicioId);

    List<Fotos> findByProyecto_IdOrderByIdDesc(Long proyectoId);
}
