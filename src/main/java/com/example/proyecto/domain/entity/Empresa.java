package com.example.proyecto.domain.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

@Entity
@Data
@Table(name = "empresa")
@RequiredArgsConstructor
public class Empresa {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String nosotros;

    @Column(nullable = false)
    private String mision;

    @Column(nullable = false)
    private String vision;

    @Column(nullable = false)
    private String direccion;

    @Column(nullable = false)
    private String ruc;

    @Column(nullable = false)
    private String numeroContacto;

    @Column(nullable = false)
    private String url1;

    @Column(nullable = false)
    private String url2;

}
