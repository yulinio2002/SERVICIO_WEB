package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Productos;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductosRepository extends JpaRepository <Productos, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
    Optional<Productos> findByNombreIgnoreCase(String nombre);
}
