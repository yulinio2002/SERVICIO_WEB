package com.example.proyecto.config.seeders;


import com.example.proyecto.domain.entity.Fotos;
import com.example.proyecto.domain.entity.Proyectos;
import com.example.proyecto.infrastructure.FotosRepository;
import com.example.proyecto.infrastructure.ProyectosRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(21)
public class DefaultProyectosSeeder implements ApplicationRunner {

    private final ProyectosRepository proyectosRepository;
    private final FotosRepository fotosRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedProyecto(
                "Proyecto Hidráulico Industrial",
                "Sistema hidráulico completo para máquina industrial...",
                "/images/img1.jpg",
                "Proyecto Hidráulico Industrial"
        );

        seedProyecto(
                "Sistema Oleohidráulico Móvil",
                "Implementación de sistema hidráulico para maquinaria móvil...",
                "/images/img2.jpg",
                "Sistema Oleohidráulico Móvil"
        );

        seedProyecto(
                "Banco de Pruebas Hidráulico",
                "Diseño e implementación de banco de pruebas para componentes...",
                "/images/img3.jpg",
                "Banco de Pruebas Hidráulico"
        );
    }

    private void seedProyecto(String nombre, String descripcion, String imageUrl, String alt) {

        Proyectos proyecto = new Proyectos();
        proyecto.setNombre(nombre);
        proyecto.setDescripcion(descripcion);

        proyecto = proyectosRepository.save(proyecto);

        Fotos foto = new Fotos();
        foto.setImagenUrl(imageUrl);
        foto.setAlt(alt);
        foto.setProyecto(proyecto);
        foto.setServicio(null); // regla XOR
        fotosRepository.save(foto);

        System.out.println("Proyecto + foto por defecto listo: " + proyecto.getNombre());
    }
}