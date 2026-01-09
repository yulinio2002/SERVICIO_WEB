package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Marcas;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MarcasRepository extends JpaRepository <Marcas, Long> {
}
