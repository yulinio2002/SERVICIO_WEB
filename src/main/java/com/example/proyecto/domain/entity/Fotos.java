package com.example.proyecto.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@Table(name = "fotos")
@RequiredArgsConstructor
public class Fotos {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String imagenUrl;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = false)
    private Servicios servicio;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyectos_id", nullable = false)
    private Proyectos proyectos;
}
