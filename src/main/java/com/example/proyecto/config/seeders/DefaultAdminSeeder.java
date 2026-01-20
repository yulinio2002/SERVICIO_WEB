package com.example.proyecto.config.seeders;

import com.example.proyecto.domain.entity.Persona;
import com.example.proyecto.domain.entity.User;
import com.example.proyecto.domain.enums.Role;
import com.example.proyecto.infrastructure.PersonRepository;
import com.example.proyecto.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.stereotype.Component;

import java.util.Set;

@Component
@RequiredArgsConstructor
public class DefaultAdminSeeder implements ApplicationRunner {

    private final UserRepository userRepository;
    private final PersonRepository personRepository;
    private final org.springframework.security.crypto.password.PasswordEncoder passwordEncoder;

    @Value("${DEFAULT_ADMIN_EMAIL:admin@demo.com}")
    private String adminEmail;

    @Value("${DEFAULT_ADMIN_PASSWORD:Admin12345*}")
    private String adminPassword;

    @Value("${DEFAULT_ADMIN_NOMBRE:Admin}")
    private String adminNombre;

    @Value("${DEFAULT_ADMIN_APELLIDO:Root}")
    private String adminApellido;

    @Value("${DEFAULT_ADMIN_TELEFONO:999999999}")
    private String adminTelefono;

    @Value("${DEFAULT_ADMIN_ADDRESS:}")
    private String adminAddress;

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        // Idempotente: si existe, no lo recrea
        if (userRepository.existsByEmail(adminEmail)) {
            return;
        }

        Persona persona = new Persona();
        persona.setNombre(adminNombre);
        persona.setApellido(adminApellido);
        persona.setTelefono(adminTelefono);
        persona = personRepository.save(persona);

        User admin = new User();
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode(adminPassword));
        admin.setAddress(adminAddress);
        admin.setPersona(persona);
        admin.setRoles(Set.of(Role.ROLE_ADMIN));

        userRepository.save(admin);

        System.out.println("Usuario ADMIN por defecto creado: " + adminEmail);
    }
}