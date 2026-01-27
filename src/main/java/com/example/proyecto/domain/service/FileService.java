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
import java.util.ArrayList;
import java.util.List;

@Transactional
@Service
@RequiredArgsConstructor
public class FileService {

    private final FotosRepository fotosRepository;
    private final ServiciosRepository serviciosRepository;
    private final ProyectosRepository proyectosRepository;

    private final String PUBLIC_DIR = "public";

    public Fotos uploadFoto(MultipartFile file,
                            String directory,
                            String alt,
                            String tipoEntidad,
                            Long entidadId) throws IOException {
        // 1. Crear entidad preliminar
        Fotos foto = new Fotos();
        foto.setAlt(alt);
        foto.setImagenUrl("");

        // 2. Asociar relaciones - IMPORTANTE: cargar la entidad completa
        if ("service".equalsIgnoreCase(tipoEntidad) && entidadId != null) {
            Fotos finalFoto1 = foto;
            serviciosRepository.findById(entidadId).ifPresent(servicio -> {
                finalFoto1.setServicio(servicio);
                // IMPORTANTE: Actualizar la relación bidireccional
                servicio.getFotos().add(finalFoto1);
            });
        } else if ("project".equalsIgnoreCase(tipoEntidad) && entidadId != null) {
            Fotos finalFoto = foto;
            proyectosRepository.findById(entidadId).ifPresent(proyecto -> {
                finalFoto.setProyecto(proyecto);
                // IMPORTANTE: Actualizar la relación bidireccional
                proyecto.setFoto(finalFoto);
            });
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
        String prefix = "/api/images/public/";
        if (fullPath == null || !fullPath.contains(prefix)) return false;

        // 1) Resolver path físico (ABSOLUTO) desde la URL
        String relativePath = fullPath.substring(fullPath.indexOf(prefix) + prefix.length())
                .replace("\\", "/");

        Path baseDir = Paths.get(PUBLIC_DIR).toAbsolutePath().normalize();
        Path filePath = baseDir.resolve(relativePath).normalize();


        // 2) Borrar archivo físico con reintento (Windows lock típico)
        boolean fileDeleted = false;
        IOException last = null;

        for (int i = 0; i < 5; i++) {
            try {
                fileDeleted = Files.deleteIfExists(filePath);
                last = null;
                break;
            } catch (IOException e) {
                last = e;
                try { Thread.sleep(200); } catch (InterruptedException ignored) {}
            }
        }
        if (last != null) throw last;

        // 3) Borrar BD (parseando ID desde el nombre) SOLO si el archivo se borró
        boolean dbDeleted = false;
        if (fileDeleted) {
            try {
                String fileName = fullPath.substring(fullPath.lastIndexOf("/") + 1);
                Long idFoto = Long.parseLong(fileName.split("_")[0]);

                if (fotosRepository.existsById(idFoto)) {
                    fotosRepository.deleteById(idFoto);
                    dbDeleted = true;
                }
            } catch (Exception e) {
                System.err.println("[DELETE] No se pudo borrar BD por parseo ID: " + e.getMessage());
            }
        }

        System.out.println("[DELETE] fileDeleted=" + fileDeleted + ", dbDeleted=" + dbDeleted);
        return fileDeleted && dbDeleted;
    }


    public List<Long> uploadGaleria(List<MultipartFile> files,
                                    String directory,
                                    String tipoEntidad,
                                    List<String> alt,
                                    Long entidadId) {
        List<Long> idFotos = new ArrayList<>();

        if (files == null || files.isEmpty()) return idFotos;

        System.out.println("DEBUG: Número de archivos: " + files.size());
        System.out.println("DEBUG: Lista alt recibida: " + (alt != null ? alt : "null"));

        for (int i = 0; i < files.size(); i++) {
            MultipartFile file = files.get(i);
            if (!file.isEmpty()) {
                try {
                    // Usar índice 'i' en lugar de idFotos.size()
                    String altText = (alt != null && i < alt.size()) ? alt.get(i) : "Galería";
                    System.out.println("DEBUG: Procesando archivo " + i + " con alt: " + altText);

                    Fotos fotoGuardada = uploadFoto(file, directory, altText, tipoEntidad, entidadId);
                    idFotos.add(fotoGuardada.getId());
                    System.out.println("DEBUG: Foto guardada con ID: " + fotoGuardada.getId());
                } catch (IOException e) {
                    System.err.println("Error subiendo foto de galería: " + file.getOriginalFilename());
                    e.printStackTrace();
                }
            }
        }

        System.out.println("DEBUG: IDs de fotos guardadas: " + idFotos);
        return idFotos;
    }
}