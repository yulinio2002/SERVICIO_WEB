package com.example.proyecto.infrastructure;

import com.example.proyecto.domain.entity.Persona;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PersonRepository extends JpaRepository<Persona, Long>{
}
