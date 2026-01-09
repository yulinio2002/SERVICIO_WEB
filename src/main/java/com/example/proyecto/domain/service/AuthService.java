package com.example.proyecto.domain.service;

import com.example.proyecto.config.*;
import com.example.proyecto.domain.entity.User;
import com.example.proyecto.domain.enums.Role;
import com.example.proyecto.dto.*;
import com.example.proyecto.email.events.WelcomeEmailEvent;
import com.example.proyecto.exception.ConflictException;
import com.example.proyecto.exception.ResourceNotFoundException;
import com.example.proyecto.exception.UnauthorizedException;
import com.example.proyecto.infrastructure.UserRepository;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Set;
import java.util.stream.Collectors;
@Service
@RequiredArgsConstructor
@Transactional
public class AuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final ApplicationEventPublisher eventPublisher;

    @Transactional
    public AuthResponseDto login(LoginDTO dto) {
        // verificar si el email ya existe, caso contrario lanzar exception
        if (!userRepository.existsByEmail(dto.getEmail())) {
            throw new UnauthorizedException("Correo no registrado");
        }
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(dto.getEmail(), dto.getPassword())
        );
        String token = tokenProvider.generateToken(auth);
        //realiza un if, para que verifique si el cliente o Proveedor y de esa manera exponer
        //el id correspondiente, esto se puede hacer mediante email-
        //return new AuthResponseDto(token);
        User user = userRepository.findByEmail(dto.getEmail())
                .orElseThrow(() -> new UnauthorizedException("Correo no registrado"));
        return new AuthResponseDto();
    }

    @Transactional
    public AuthMeDto getCurrentUserInfo(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("Usuario no encontrado"));

        Set<String> roles = user.getRoles().stream()
                .map(Role::name)
                .collect(Collectors.toSet());
        return new AuthMeDto();
    }
}