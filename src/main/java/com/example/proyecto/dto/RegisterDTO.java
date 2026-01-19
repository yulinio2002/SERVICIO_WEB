package com.example.proyecto.dto;

import com.example.proyecto.domain.entity.Persona;
import lombok.Data;

@Data
public class RegisterDTO {
    private String email ;
    private String password;
    private String direccion;
    Persona persona;
}
