package com.example.proyecto.domain.service;

import com.example.proyecto.domain.entity.Persona;
import com.example.proyecto.domain.entity.User;
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

    public void updateUser(Long userId, UpdateUserDTO dto) {

        User user = usuarioRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("Usuario no encontrado"));

        // 🔹 Actualizar email
        if (dto.getEmail() != null) {
            user.setEmail(dto.getEmail());
        }

        // 🔹 Actualizar teléfono (Persona)
        if (dto.getTelefono() != null) {
            Persona persona = user.getPersona();
            if (persona == null) {
                throw new RuntimeException("El usuario no tiene persona asociada");
            }
            persona.setTelefono(dto.getTelefono());
            personaRepository.save(persona);
        }

        usuarioRepository.save(user);
    }


}
