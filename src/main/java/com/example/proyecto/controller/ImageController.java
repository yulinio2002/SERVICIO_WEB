package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.Fotos;
import com.example.proyecto.domain.service.FileService;
import com.example.proyecto.infrastructure.FotosRepository;
import com.example.proyecto.infrastructure.ProyectosRepository;
import com.example.proyecto.infrastructure.ServiciosRepository;
import jakarta.servlet.http.HttpServletRequest;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.Optional;
import java.util.stream.Stream;

@Data
class requestFileBody {
    private String filename;
}


@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final FotosRepository fotosRepository;
    private final ServiciosRepository serviciosRepository; // Necesarios para buscar la relación
    private final ProyectosRepository proyectosRepository;
    private final FileService fileService;

    private final String PUBLIC_DIR = "public/";

    @PostMapping("/upload")
    public ResponseEntity<Fotos> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("directory") String directory,
            @RequestParam("alt") String alt,
            @RequestParam(value = "entity", required = false) String tipoEntidad, // "service", "project", "brand", "producto", etc.
            @RequestParam(value = "entidadId", required = false) Long entidadId) {

        if (file.isEmpty()) {
            // retornar error si el archivo está vacío
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            // 0. Crear y guardar la entidad "preliminar" para obtener el ID
            Fotos foto = new Fotos();
            foto.setAlt(alt);
            foto.setImagenUrl(""); // Aún no tenemos la URL final

            // Lógica para asociar con Servicio o Proyecto (si aplica)
            if ("service".equalsIgnoreCase(tipoEntidad) && entidadId != null) {
                serviciosRepository.findById(entidadId).ifPresent(foto::setServicio);
            } else if ("product".equalsIgnoreCase(tipoEntidad) && entidadId != null) {
                proyectosRepository.findById(entidadId).ifPresent(foto::setProyecto);
            }

            foto = fotosRepository.save(foto);

            // 1. Creamos el directorio si no existe
            Path uploadPath = Paths.get("public/");
            if(directory != null) {
                uploadPath = Paths.get("public/" + directory + "/");
            }

            if (!Files.exists(uploadPath)) {
                Files.createDirectories(uploadPath);
            }

            // 2. Construimos el nuevo nombre: id_entity_nombreOriginal.ext
            String originalFileName = file.getOriginalFilename();

            // Limpiamos el nombre original por seguridad (opcional pero recomendado)
            // para evitar rutas extrañas si el archivo viene con paths completos
            String cleanFileName = Paths.get(originalFileName).getFileName().toString();

            String newFileName = foto.getId() + "_" + cleanFileName;

            // 3. Resolvemos la ruta final con el nuevo nombre
            Path filePath = uploadPath.resolve(newFileName);

            Files.copy(file.getInputStream(), filePath, StandardCopyOption.REPLACE_EXISTING);

            // 4. Actualizamos la URL de la imagen en la entidad
            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/images/public/") // Usamos tu endpoint de acceso
                    .path(directory + "/")
                    .path(newFileName)
                    .toUriString();
            foto.setImagenUrl(fileUrl);
            fotosRepository.save(foto); // Actualizamos la BD

            return ResponseEntity.ok(foto);

        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    // --- OBTENER IMAGEN (Búsqueda dinámica) ---
    // Uso: GET /api/images/{directory}/{entity}/{id}
    @GetMapping("/public/**")
    public ResponseEntity<Resource> getImage(HttpServletRequest request) {
        try {
            // 1. Extraer la ruta relativa de la URL
            // La URL completa es: /api/images/public/servicios/55_foto.jpg
            // Queremos solo: servicios/55_foto.jpg
            String fullPath = request.getRequestURI();
            String prefix = "/api/images/public/";

            // Seguridad básica: evitar que suban niveles con ".."
            if (fullPath.contains("..")) {
                return ResponseEntity.badRequest().build();
            }

            // Obtenemos la parte de la ruta que nos interesa
            String relativePath = fullPath.substring(fullPath.indexOf(prefix) + prefix.length());

            // 2. Construir la ruta al archivo físico
            // Base "public/" + "servicios/55_foto.jpg"
            Path filePath = Paths.get(PUBLIC_DIR).resolve(relativePath).normalize();
            Resource resource = new UrlResource(filePath.toUri());

            if (resource.exists() && resource.isReadable()) {
                String contentType = Files.probeContentType(filePath);
                if (contentType == null) contentType = "application/octet-stream";

                return ResponseEntity.ok()
                        .contentType(MediaType.parseMediaType(contentType))
                        .body(resource);
            } else {
                return ResponseEntity.notFound().build();
            }

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }

    @DeleteMapping("/public/**")
    public ResponseEntity<String> deleteFile(HttpServletRequest request) {
        String fullPath = request.getRequestURI();

        // Llamamos al servicio para borrar el archivo físico
        boolean deleted = fileService.deletePhysicalFile(fullPath);

        // Eliminamos también la referencia en la base de datos si existe
        boolean dbDeleted = fileService.deleteFromDatabaseIfExists(fullPath);

        if (deleted) {
            return ResponseEntity.ok("Archivo eliminado correctamente");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se pudo encontrar el archivo");
        }
    }

}