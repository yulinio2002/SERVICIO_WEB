package com.example.proyecto.dto;

import lombok.Data;
import lombok.RequiredArgsConstructor;

import java.util.Set;
@RequiredArgsConstructor
@Data
public class AuthMeDto {
    private Long id;
    //private String nombre;
    private String email;
    //private String telefono;
    private Set<String> role;

  public AuthMeDto(Long id, String email, Set<String> roles) {
      this.id = id;
      this.email = email;
      this.role = roles;
  }

}