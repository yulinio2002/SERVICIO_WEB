package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Servicios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServiciosRepository extends JpaRepository<Servicios, Long> {
    List<Servicios> findTop5ByOrderByIdDesc();
}
