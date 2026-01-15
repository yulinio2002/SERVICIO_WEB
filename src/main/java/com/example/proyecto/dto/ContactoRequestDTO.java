package com.example.proyecto.dto;
import lombok.Data;

@Data
public class ContactoRequestDTO {
    private String nombre;
    private String apellidos;
    private String empresa;
    private String correo;
    private String mensaje;
}
