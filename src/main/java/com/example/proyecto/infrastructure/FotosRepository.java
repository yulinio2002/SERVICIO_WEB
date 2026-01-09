package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Fotos;
import org.springframework.data.jpa.repository.JpaRepository;

public interface FotosRepository extends JpaRepository <Fotos, Long> {
}
