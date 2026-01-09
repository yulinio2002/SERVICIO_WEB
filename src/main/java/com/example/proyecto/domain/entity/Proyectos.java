package com.example.proyecto.domain.entity;
import com.example.proyecto.domain.entity.Fotos;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.List;

@Data
@Entity
@Table(name="proyectos")
@RequiredArgsConstructor
public class Proyectos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String img_url;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @OneToMany(mappedBy = "proyectos", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    private List<Fotos> fotos;

}
