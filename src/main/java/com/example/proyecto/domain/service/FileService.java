package com.example.proyecto.domain.service;

import com.example.proyecto.infrastructure.FotosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Transactional
@Service
@RequiredArgsConstructor
public class FileService {

    private final FotosRepository fotosRepository;

    private final String PUBLIC_DIR = "public";

    /**
     * Borra un archivo físico basado en su URL pública o ruta relativa.
     * @param fileUrl URL completa o ruta (ej: /api/images/public/servicios/55_foto.jpg)
     * @return true si se borró, false si no existía.
     */
    public boolean deletePhysicalFile(String fileUrl) {
        try {
            String prefix = "/api/images/public/";
            if (!fileUrl.contains(prefix)) return false;

            // Extraemos la ruta relativa después del prefijo
            String relativePath = fileUrl.substring(fileUrl.indexOf(prefix) + prefix.length());

            // Construimos la ruta: public/servicios/55_foto.jpg
            Path filePath = Paths.get(PUBLIC_DIR).resolve(relativePath).normalize();

            return Files.deleteIfExists(filePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * Borrar el registro del archivo en la base de datos si existe.
     *
     */
    public boolean deleteFromDatabaseIfExists(String fullPath) {
        try {
            // 1. Extraer el nombre del archivo de la ruta (ej: "55_foto.jpg")
            // Buscamos la última barra diagonal "/"
            String fileName = fullPath.substring(fullPath.lastIndexOf("/") + 1);

            // 2. Extraer el ID (la parte antes del primer "_")
            // Usamos split("_")[0] para obtener el primer segmento
            String idStr = fileName.split("_")[0];

            // 3. Convertir el texto a Long
            Long idFoto = Long.parseLong(idStr);

            // 4. Lógica de eliminación en BD
            if (fotosRepository.existsById(idFoto)) {
                fotosRepository.deleteById(idFoto);
                return true;
            }
        } catch (Exception e) {
            // Captura errores si el formato del nombre no es el esperado o no es un número
            System.err.println("Error al extraer ID de la ruta: " + e.getMessage());
        }

        return false;
    }
}