package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Servicios;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ServicioRepository extends JpaRepository<Servicios, Long>, JpaSpecificationExecutor<Servicios> {
    List<Servicios> findByActivoTrue();
    List<Servicios> findByProveedorIdAndActivoTrue(Long proveedorId);
    List<Servicios> findByProveedorId(Long proveedorId);
}
