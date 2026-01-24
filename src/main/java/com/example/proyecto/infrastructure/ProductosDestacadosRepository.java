package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.ProductosDestacados;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface ProductosDestacadosRepository extends JpaRepository<ProductosDestacados, Long> {
    boolean existsByIdProducto(Long idProducto);
    boolean deleteByIdProducto(Long idProducto);

    Optional<ProductosDestacados> findByIdProducto(Long idProducto);
}
