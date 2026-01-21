package com.example.proyecto.controller;

import com.example.proyecto.config.JwtTokenProvider;
import com.example.proyecto.domain.entity.Persona;
import com.example.proyecto.domain.entity.User;
import com.example.proyecto.domain.service.AuthService;
import com.example.proyecto.domain.service.PersonService;
import com.example.proyecto.dto.*;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;
    private final JwtTokenProvider tokenProvider;
    private final PersonService personService;
    @PostMapping("/login")
    public ResponseEntity<AuthResponseDto> login(@Valid @RequestBody LoginDTO dto) {
        AuthResponseDto resp = authService.login(dto);
        return ResponseEntity.ok(resp);
    }

    @GetMapping("/me")
    public ResponseEntity<AuthMeDto> getCurrentUser(Principal principal) {
        String email = principal.getName();
        AuthMeDto userInfo = authService.getCurrentUserInfo(email);
        return ResponseEntity.ok(userInfo);
    }


    @PostMapping("/register")
    public ResponseEntity<?> register(@RequestBody RegisterDTO registerDTO) {

        personService.register(registerDTO);

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body("Usuario registrado correctamente");
    }

    @GetMapping
    public List<UserDTO> getAll() {
        return personService.getAll().stream().map(u -> {
            UserDTO dto = new UserDTO();
            dto.setId(u.getId());
            dto.setEmail(u.getEmail());
            dto.setDireccion(u.getAddress()); // o getDireccion() según tu entidad

            PersonaDTO p = new PersonaDTO();
            if (u.getPersona() != null) {
                p.setNombre(u.getPersona().getNombre());
                p.setApellido(u.getPersona().getApellido());
                p.setTelefono(u.getPersona().getTelefono());
            }
            dto.setPersona(p);
            dto.setRoles(u.getRoles());
            return dto;
        }).toList();
    }
    @PutMapping("/{id}")
    public ResponseEntity<Void> updateUser(
            @PathVariable Long id,
            @RequestBody UpdateUserDTO dto
    ) {
        personService.updateUser(id, dto);
        return ResponseEntity.noContent().build(); // 204
    }



}