package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Marcas;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface MarcasRepository extends JpaRepository <Marcas, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    Optional<Marcas> findByNombreIgnoreCase(String nombre);
}
