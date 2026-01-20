package com.example.proyecto.config.seeders;

import com.example.proyecto.domain.entity.Marcas;
import com.example.proyecto.infrastructure.MarcasRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Order(21)
public class DefaultMarcasSeeder implements ApplicationRunner {

    private final MarcasRepository marcasRepository;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        seed("Atos", "/images/img2.jpg");
        seed("OMT", "/images/img1.jpg");
        seed("Danfoss Eaton", "/images/img3.jpg");
    }

    private void seed(String nombre, String imagenUrl) {
        if (marcasRepository.existsByNombreIgnoreCase(nombre)) return;

        Marcas m = new Marcas();
        m.setNombre(nombre);
        m.setImagenUrl(imagenUrl);
        marcasRepository.save(m);

        System.out.println("Marca por defecto lista: " + nombre);
    }
}
