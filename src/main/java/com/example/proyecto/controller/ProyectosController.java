package com.example.proyecto.controller;

import com.example.proyecto.domain.entity.Fotos;
import com.example.proyecto.domain.entity.Proyectos;
import com.example.proyecto.domain.enums.Categorias;
import com.example.proyecto.domain.service.FileService;
import com.example.proyecto.domain.service.ProyectosService;
import com.example.proyecto.dto.ProyectoResponseDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequestMapping("/api/proyectos")
@RequiredArgsConstructor
public class ProyectosController {

    private final ProyectosService proyectosService;
    private final FileService fileService;

    /*
    Crear un proyecto con imagen asociada
    La imagen se sube y se asocia al proyecto en la misma petición; por lo que es obligatorio enviar la imagen.
    1. Se crea el proyecto (sin imagen)
    2. Se sube la imagen y se asocia al proyecto creado
    3. Se retorna el proyecto creado con la imagen asociada
     */
    @PostMapping
    public ResponseEntity<ProyectoResponseDTO> create(@RequestParam("file") MultipartFile file,
                                            @RequestParam("nombre") String nombre,
                                            @RequestParam("descripcion") String descripcion) {
        Proyectos request = new Proyectos();
        request.setNombre(nombre);
        request.setDescripcion(descripcion);

        // Creamos la imagen y la asociamos al proyecto
        // Si la imagen está vacia, retornamos error IllegalArgumentException
        if (file == null || file.isEmpty()) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        try{
            // Guardamos el proyecto primero para obtener su ID
            Proyectos proyectoCreado = proyectosService.create(request);

            var foto = fileService.uploadFoto(file, "projects", nombre, "project", proyectoCreado.getId());
            //request.setFoto(foto);
            // Mapear el proyecto con la foto asociada
            ProyectoResponseDTO proyectoResponseDTO = new ProyectoResponseDTO();
            proyectoResponseDTO.setId(proyectoCreado.getId());
            proyectoResponseDTO.setDescripcion((proyectoCreado.getDescripcion()));
            proyectoResponseDTO.setNombre(proyectoCreado.getNombre());
            proyectoResponseDTO.setFoto(new ProyectoResponseDTO.Image(
                    foto.getId(),
                    foto.getImagenUrl(),
                    foto.getAlt()
            ));

            return ResponseEntity.status(HttpStatus.CREATED).body(proyectoResponseDTO);

        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }

    }

    @GetMapping("/{id}")
    public ResponseEntity<ProyectoResponseDTO> getById(@PathVariable Long id) {
        return ResponseEntity.ok(proyectosService.getById(id));
    }

    @GetMapping
    public ResponseEntity<List<Proyectos>> getAll() {
        return ResponseEntity.ok(proyectosService.getAll());
    }

    @GetMapping("/top/5")
    public ResponseEntity<List<Proyectos>> top5() {
        return ResponseEntity.ok(proyectosService.top5ByIdDesc());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Proyectos> update(@PathVariable Long id,
                                            @RequestParam("file") MultipartFile file,
                                            @RequestParam("nombre") String nombre,
                                            @RequestParam("descripcion") String descripcion) {
        // Si la imagen se ha enviado vacía, no se actualiza
        Proyectos request = new Proyectos();
        request.setNombre(nombre);
        request.setDescripcion(descripcion);

        if (file != null && !file.isEmpty()) {
            try {
                // Primero eliminar la imagen anterior
                String existingImgUrl = proyectosService.getById(id).getFoto().getUrl();
                fileService.deleteFotoComplete(existingImgUrl);

                // Luego crear la nueva imagen
                var fotoNueva  = fileService.uploadFoto(file, "projects", "Proyecto: " + nombre, "project", id);
                request.setFoto(fotoNueva);
            } catch (Exception e) {
                throw new RuntimeException("Error al crear la imagen del producto: " + e.getMessage());
            }
        }
        return ResponseEntity.ok(proyectosService.update(id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        // Eliminar proyecto y su imagen asociada
        String fotoUrl = proyectosService.getById(id).getFoto().getUrl();
        try {
            fileService.deleteFotoComplete(fotoUrl);
        } catch (Exception e) {
            throw new RuntimeException("Error al eliminar la imagen del proyecto: " + e.getMessage());
        }
        proyectosService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
