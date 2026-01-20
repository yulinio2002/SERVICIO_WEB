package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Productos;
import com.example.proyecto.domain.enums.Categorias;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
@Repository
public interface ProductosRepository extends JpaRepository <Productos, Long> {
    boolean existsByNombreIgnoreCase(String nombre);
    boolean existsByNombreIgnoreCaseAndIdNot(String nombre, Long id);
    Optional<Productos> findByNombreIgnoreCase(String nombre);

    // Top 5 por ID (desc)
    List<Productos> findTop5ByOrderByIdDesc();

    // Filtrar + ordenar por nombre
    List<Productos> findByCategoriasOrderByNombreAsc(Categorias categorias);

    List<Productos> findByMarcaIgnoreCaseOrderByNombreAsc(String marca);

    // Ordenar TODOS
    List<Productos> findAllByOrderByCategoriasAscNombreAsc();

    List<Productos> findAllByOrderByMarcaAscNombreAsc();

    Optional<Productos> findFirstByMarcaIgnoreCaseOrderByIdAsc(String marca);
}
