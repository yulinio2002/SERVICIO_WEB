package com.example.proyecto.domain.entity;

import com.example.proyecto.domain.enums.Categorias;
import jakarta.persistence.*;
import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Data
@Entity
@Table(name = "productos")
@RequiredArgsConstructor
public class Productos {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String nombre;

    @Column(nullable = false)
    private String img_url;

    @Column(nullable = false, length = 1000)
    private String descripcion;

    @Column(nullable = false, length = 5000)
    private String content;

    @Column(length = 5000)
    private String features = "";       // La lista de características, están separadas por ;

    @Column(nullable = false)
    private String marca;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "producto_categoria", joinColumns = @JoinColumn(name = "productos_id"))
    @Enumerated(EnumType.STRING)
    @Column(name = "categoria")
    private Set<Categorias> categorias = new HashSet<>();
}
