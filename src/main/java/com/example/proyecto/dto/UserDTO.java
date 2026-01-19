package com.example.proyecto.dto;

import lombok.Data;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String direccion;
    private PersonaDTO persona;
}
