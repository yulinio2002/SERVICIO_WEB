package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Productos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductosRepository extends JpaRepository <Productos, Long> {
}
