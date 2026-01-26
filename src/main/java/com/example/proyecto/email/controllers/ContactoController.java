package com.example.proyecto.email.controllers;

import com.example.proyecto.dto.ContactoRequestDTO;
import com.example.proyecto.email.events.ContactoCreadoEvent;
import com.example.proyecto.email.service.EmailService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/contacto")
@RequiredArgsConstructor
public class ContactoController {
    private final ApplicationEventPublisher publisher;
    private final EmailService emailService;


    @PostMapping
    public ResponseEntity<Void> enviar(@RequestBody ContactoRequestDTO request) {
        validate(request);
        publisher.publishEvent(new ContactoCreadoEvent(this, request));
        return ResponseEntity.accepted().build(); // 202 Accepted
    }

    private void validate(ContactoRequestDTO r) {
        if (r == null) throw new IllegalArgumentException("El body no puede ser null.");
        if (isBlank(r.getNombre())) throw new IllegalArgumentException("Nombre es obligatorio.");
        if (isBlank(r.getApellidos())) throw new IllegalArgumentException("Apellidos es obligatorio.");
        if (isBlank(r.getEmpresa())) throw new IllegalArgumentException("Empresa es obligatorio.");
        if (isBlank(r.getCorreo())) throw new IllegalArgumentException("Correo es obligatorio.");
        if (isBlank(r.getMensaje())) throw new IllegalArgumentException("Mensaje es obligatorio.");
    }

    private boolean isBlank(String s) {
        return s == null || s.trim().isEmpty();
    }

}
