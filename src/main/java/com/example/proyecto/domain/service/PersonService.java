package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Persona;
import com.example.proyecto.domain.entity.User;
import com.example.proyecto.domain.enums.Role;
import com.example.proyecto.dto.PersonaDTO;
import com.example.proyecto.dto.RegisterDTO;
import com.example.proyecto.dto.UpdateUserDTO;
import com.example.proyecto.dto.UserDTO;
import com.example.proyecto.infrastructure.PersonRepository;
import com.example.proyecto.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@Transactional
@RequiredArgsConstructor
public class PersonService {
    private final UserRepository usuarioRepository;
    private final PersonRepository personaRepository;
    private final PasswordEncoder passwordEncoder;
    public void register(RegisterDTO registerDTO) {

        // Validación básica
        if (usuarioRepository.existsByEmail(registerDTO.getEmail())) {
            throw new RuntimeException("El email ya está registrado");
        }

        // Guardar Persona
        Persona persona = personaRepository.save(registerDTO.getPersona());

        // Crear Usuario
        User usuario = new User();
        usuario.setEmail(registerDTO.getEmail());
        usuario.setPassword(passwordEncoder.encode(registerDTO.getPassword()));
        usuario.setAddress(registerDTO.getDireccion());
        usuario.setPersona(persona);
        usuario.setRoles(registerDTO.getRoles()); // Asignar rol por defecto
        usuarioRepository.save(usuario);
    }

    public List<User> getAll() {
        return usuarioRepository.findAll();
    }

    private UserDTO toDto(User u) {
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

        return dto;
    }

    @Transactional
    public void updateUser(Long userId, UpdateUserDTO request) {

        User user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // EMAIL
        if (request.getEmail() != null && !request.getEmail().isBlank()) {
            user.setEmail(request.getEmail().trim());
        }

        // PASSWORD (solo si viene)
        if (request.getPassword() != null && !request.getPassword().isBlank()) {
            user.setPassword(passwordEncoder.encode(request.getPassword()));
        }

        // ADDRESS
        if (request.getAddress() != null) {
            user.setAddress(request.getAddress().trim());
        }

        // ROLES
       if (request.getRoles() != null && !request.getRoles().isEmpty()) {
           user.setRoles(request.getRoles());
       }

        // PERSONA
        if (request.getPersona() != null) {
            Persona persona = user.getPersona();

            if (persona == null) {
                persona = new Persona();
                user.setPersona(persona);
            }

            if (request.getPersona().getNombre() != null) {
                persona.setNombre(request.getPersona().getNombre().trim());
            }

            if (request.getPersona().getApellido() != null) {
                persona.setApellido(request.getPersona().getApellido().trim());
            }

            if (request.getPersona().getTelefono() != null) {
                persona.setTelefono(request.getPersona().getTelefono().trim());
            }
        }

        usuarioRepository.save(user);
    }



}
