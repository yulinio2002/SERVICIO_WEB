package com.example.proyecto.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIdentityInfo;
import com.fasterxml.jackson.annotation.ObjectIdGenerators;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@JsonIdentityInfo(
        generator = ObjectIdGenerators.PropertyGenerator.class,
        property = "id"
)
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

    @Column(nullable = false)
    private String alt;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "servicio_id", nullable = true)
    @JsonBackReference("servicio-fotos")
    private Servicios servicio;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "proyectos_id", nullable = true, unique = true)
    @JsonBackReference("proyecto-fotos")
    private Proyectos proyecto;
}
