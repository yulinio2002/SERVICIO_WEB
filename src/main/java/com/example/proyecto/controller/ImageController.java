package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.Fotos;
import com.example.proyecto.domain.service.FileService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@RequestMapping("/api/images")
@RequiredArgsConstructor
public class ImageController {

    private final FileService fileService;
    private final String PUBLIC_DIR = "public/";

    @PostMapping("/upload")
    public ResponseEntity<Fotos> uploadImage(
            @RequestParam("file") MultipartFile file,
            @RequestParam("directory") String directory,
            @RequestParam("alt") String alt,
            @RequestParam(value = "entity", required = false) String tipoEntidad,
            @RequestParam(value = "entidadId", required = false) Long entidadId) {

        if (file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }

        try {
            Fotos foto = fileService.uploadFoto(file, directory, alt, tipoEntidad, entidadId);
            return ResponseEntity.ok(foto);
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @GetMapping("/public/**")
    public ResponseEntity<Resource> getImage(HttpServletRequest request) {
        try {
            String fullPath = request.getRequestURI();
            String prefix = "/api/images/public/";

            if (fullPath.contains("..")) {
                return ResponseEntity.badRequest().build();
            }

            String relativePath = fullPath.substring(fullPath.indexOf(prefix) + prefix.length());
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

        try {
            boolean deleted = fileService.deleteFotoComplete(fullPath);

            if (deleted) {
                return ResponseEntity.ok("Imagen eliminada correctamente");
            } else {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body("No se pudo encontrar o eliminar la imagen");
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error al eliminar la imagen");
        }
    }
}