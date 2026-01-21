package com.example.proyecto.dto;

import com.example.proyecto.domain.entity.Persona;
import com.example.proyecto.domain.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class RegisterDTO {
    private String email ;
    private String password;
    private String direccion;
    Persona persona;
    private Set<Role> roles;
}
