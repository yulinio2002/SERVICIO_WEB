package com.example.proyecto.dto;

import com.example.proyecto.domain.enums.Role;
import lombok.Data;

import java.util.Set;

@Data
public class UpdateUserDTO {
    private String email;
    private String password;
    private String address;
    private Set<Role> roles;
    private String nombre;
    private PersonaDTO persona;

}