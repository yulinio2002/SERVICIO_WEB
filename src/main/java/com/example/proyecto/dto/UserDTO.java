package com.example.proyecto.dto;

import com.example.proyecto.domain.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UserDTO {
    private Long id;
    private String email;
    private String direccion;
    private PersonaDTO persona;
    private Set<Role> roles;
}
