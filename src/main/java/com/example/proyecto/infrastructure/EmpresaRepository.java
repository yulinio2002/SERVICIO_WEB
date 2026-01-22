package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Empresa;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EmpresaRepository extends JpaRepository<Empresa, Long> {
    // Método derivado para comprobar existencia por nombre (case-insensitive)
    boolean existsByNombreIgnoreCase(String nombre);
}
