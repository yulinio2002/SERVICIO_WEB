package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Fotos;
import com.example.proyecto.infrastructure.FotosRepository;
import com.example.proyecto.infrastructure.ProyectosRepository;
import com.example.proyecto.infrastructure.ServiciosRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Transactional
@Service
@RequiredArgsConstructor
public class FileService {

    private final FotosRepository fotosRepository;
    private final ServiciosRepository serviciosRepository;
    private final ProyectosRepository proyectosRepository;

    private final String PUBLIC_DIR = "public";

    public Fotos uploadFoto(MultipartFile file, String directory, String alt, String tipoEntidad, Long entidadId) throws IOException {
        // 1. Crear entidad preliminar
        Fotos foto = new Fotos();
        foto.setAlt(alt);
        foto.setImagenUrl("");

        // 2. Asociar relaciones
        if ("service".equalsIgnoreCase(tipoEntidad) && entidadId != null) {
            serviciosRepository.findById(entidadId).ifPresent(foto::setServicio);
        } else if ("project".equalsIgnoreCase(tipoEntidad) && entidadId != null) {
            proyectosRepository.findById(entidadId).ifPresent(foto::setProyecto);
        }

        // 3. Guardar para generar ID
        foto = fotosRepository.save(foto);

        // 4. Preparar nombre y guardar archivo físico
        String cleanFileName = file.getOriginalFilename().replaceAll("[^a-zA-Z0-9._-]", "_"); // Saneamiento básico
        String newFileName = foto.getId() + "_" + cleanFileName;

        Path uploadPath = Paths.get(PUBLIC_DIR, directory);

        if (!Files.exists(uploadPath)) {
            Files.createDirectories(uploadPath);
        }

        Path filePath = uploadPath.resolve(newFileName);
        Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);


        // 5. Generar URL pública y actualizar BD
        String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                .path("/api/images/public/")
                .path(directory + "/")
                .path(newFileName)
                .toUriString();

        foto.setImagenUrl(fileUrl);
        return fotosRepository.save(foto);
    }

    public boolean deleteFotoComplete(String fullPath) throws IOException {
        boolean dbDeleted = false;

        // 1. Intentar borrar de la BD extrayendo el ID del nombre del archivo
        try {
            // Asumimos formato URL: .../directory/ID_nombre.ext
            String fileName = fullPath.substring(fullPath.lastIndexOf("/") + 1);
            String idStr = fileName.split("_")[0];
            Long idFoto = Long.parseLong(idStr);

            if (fotosRepository.existsById(idFoto)) {
                fotosRepository.deleteById(idFoto);
                dbDeleted = true;
            }
        } catch (Exception e) {
            // Si falla el parseo, seguimos intentando borrar el archivo físico
            System.err.println("No se pudo extraer ID para borrar de BD: " + e.getMessage());
        }

        // 2. Borrar archivo físico
        boolean fileDeleted = false;

        // Normalizamos para evitar problemas de barras en diferentes SO
        String prefix = "/api/images/public/";
        if (!fullPath.contains(prefix)) return false;

        String relativePath = fullPath.substring(fullPath.indexOf(prefix) + prefix.length());
        // Decodificamos o reemplazamos barras invertidas si vienen de Windows
        relativePath = relativePath.replace("\\", "/");

        Path filePath = Paths.get(PUBLIC_DIR).resolve(relativePath).normalize();

        fileDeleted = Files.deleteIfExists(filePath);

        return dbDeleted || fileDeleted;
    }
}