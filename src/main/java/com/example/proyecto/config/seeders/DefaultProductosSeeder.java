package com.example.proyecto.config.seeders;

import com.example.proyecto.domain.entity.Productos;
import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.infrastructure.ProductosRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Set;

@Component
@RequiredArgsConstructor
@Order(22)
public class DefaultProductosSeeder implements ApplicationRunner {

    private final ProductosRepository productosRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {

        seedProducto(
                "Campanas de Motor Bomba desde 0.75 HP - 150 HP",
                "/images/img1.jpg",
                "Campanas para motor bomba con diferentes capacidades.",
                "Consúltanos por compatibilidad y disponibilidad.",
                "Rango 0.75 HP - 150 HP;Materiales industriales;Alta precisión",
                "OMT",
                Set.of(Categorias.ACCESORIOS_HIDRAULICOS)
        );

        seedProducto(
                "Conectores",
                "/images/img2.jpg",
                "Conectores industriales para aplicaciones hidráulicas.",
                "Modelos disponibles para distintas configuraciones.",
                "Distintos formatos;Alta durabilidad;Uso industrial",
                "Atos",
                Set.of(Categorias.ACCESORIOS_HIDRAULICOS)
        );

        seedProducto(
                "Bombas de Desplazamiento Fijo",
                "/images/img3.jpg",
                "Bombas de paletas, pistones radiales y manuales para media / alta presión.",
                "Contamos con un amplio stock de los diversos tipos de Bombas de Desplazamiento Fijo.",
                "Paletas hasta 150 cm³/rev;Pistones radiales hasta 25 cm³/rev;Manual hasta 20 cm³/rev",
                "Atos",
                Set.of(Categorias.BOMBAS_HIDRAULICAS)
        );

        seedProducto(
                "Abrazaderas",
                "/images/img1.jpg",
                "Abrazaderas para montaje de tuberías y accesorios.",
                "Disponibles en diferentes tamaños y materiales.",
                "Acero y polímero;Alta resistencia;Instalación rápida",
                "Danfoss Eaton",
                Set.of(Categorias.ABRAZADERAS)
        );
    }

    private void seedProducto(
            String nombre,
            String imgUrl,
            String descripcion,
            String content,
            String features,
            String marca,
            Set<Categorias> categorias
    ) {
        if (productosRepository.existsByNombreIgnoreCase(nombre)) return;

        Productos p = new Productos();
        p.setNombre(nombre);
        p.setImg_url(imgUrl);
        p.setDescripcion(descripcion);
        p.setContent(content);
        p.setFeatures(features);
        p.setMarca(marca);
        p.setCategorias(categorias);

        productosRepository.save(p);
        System.out.println("Producto por defecto listo: " + nombre);
    }
}
