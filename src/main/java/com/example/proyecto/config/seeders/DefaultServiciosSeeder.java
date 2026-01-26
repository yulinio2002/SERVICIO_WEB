package com.example.proyecto.config.seeders;
import com.example.proyecto.domain.entity.Fotos;
import com.example.proyecto.domain.entity.Servicios;
import com.example.proyecto.infrastructure.FotosRepository;
import com.example.proyecto.infrastructure.ServiciosRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
@Order(20)
public class DefaultServiciosSeeder implements ApplicationRunner {

    private final ServiciosRepository serviciosRepository;
    private final FotosRepository fotosRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seedServicio(
                "Fabricación de Sistemas",
                "Diseño y fabricación de sistemas hidráulicos y mecánicos integrales.",
                "Desarrollamos sistemas completos a medida, desde la ingeniería básica hasta la fabricación, montaje y pruebas finales.",
                "/images/img1.jpg",
                "Fabricación de Sistemas"
        );

        seedServicio(
                "Control y Automatización",
                "Soluciones de control y automatización industrial.",
                "Implementamos sistemas de control, automatización y monitoreo para optimizar procesos industriales.",
                "/images/img2.jpg",
                "Control y Automatización"
        );

        seedServicio(
                "Fabricación de Piezas a Medida",
                "Fabricación de componentes personalizados según requerimiento.",
                "Fabricamos piezas especiales bajo plano o muestra, garantizando precisión y calidad.",
                "/images/img3.jpg",
                "Fabricación de Piezas a Medida"
        );

        seedServicio(
                "Suministro e Instalación de Tuberías",
                "Suministro e instalación profesional de tuberías industriales.",
                "Ofrecemos soluciones completas en suministro, montaje e instalación de tuberías hidráulicas e industriales.",
                "/images/img1.jpg",
                "Instalación de Tuberías"
        );

    }

    private void seedServicio(String title,
                         String description,
                         String content,
                         String imageUrl,
                         String alt) {
        Servicios servicio = new Servicios();
        servicio.setNombre(title);
        servicio.setDescripcion(description);
        servicio.setContent(content);
        // Juntamos en un solo string separando por ;
        servicio.setFeatures(List.of(
                "Cálculos: Potencia mecánica, fuerza, velocidad y exigencias (estáticas y dinámicas)",
                "Selección del actuador",
                "Cálculos de mecánica de fluidos",
                "Cálculos de disipación de calor",
                "Diagrama hidráulico y lista de materiales (BOM)",
                "Documentación técnica"
        ).stream().reduce((a, b) -> a + ";" + b).orElse(""));

        servicio = serviciosRepository.save(servicio);


        // Sembrar fotos relacionadas (idempotente por url)
        seedFoto(servicio, imageUrl, alt);

        System.out.println("Servicio + fotos por defecto listo: " + servicio.getNombre());
    }

    private void seedFoto(Servicios servicio, String url, String alt) {
        Fotos f = new Fotos();
        f.setImagenUrl(url);
        f.setAlt(alt);
        f.setServicio(servicio);
        f.setProyecto(null); // por tu regla XOR (solo uno)
        fotosRepository.save(f);
    }
}
