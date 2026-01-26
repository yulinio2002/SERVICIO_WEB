package com.example.proyecto.email.listeners;

import com.example.proyecto.email.events.ContactoCreadoEvent;
import com.example.proyecto.email.service.EmailService;
import com.example.proyecto.exception.AsyncOperationInterruptedException;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class ContactoEmailListener {

    private final EmailService emailService;

    @Value("${empresa.email}")
    private String empresaEmail;


    @EventListener
    @Async
    public void onContactoCreado(ContactoCreadoEvent contactoCreadoEvent){
        try {
            Thread.sleep(2000);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new AsyncOperationInterruptedException("El hilo de envio de email de Formulario de contacto fue interrumpido: " + e);
        }

        Map<String,Object> vars = new HashMap<>();
        vars.put("nombre", contactoCreadoEvent.getContactoRequestDTO().getNombre());
        vars.put("apellidos", contactoCreadoEvent.getContactoRequestDTO().getApellidos());
        vars.put("empresa", contactoCreadoEvent.getContactoRequestDTO().getEmpresa());
        vars.put("telefono", contactoCreadoEvent.getContactoRequestDTO().getTelefono());
        vars.put("correo", contactoCreadoEvent.getContactoRequestDTO().getCorreo());
        vars.put("mensaje", contactoCreadoEvent.getContactoRequestDTO().getMensaje());
        vars.put("motivo", contactoCreadoEvent.getContactoRequestDTO().getMotivo());
        vars.put("fecha", LocalDateTime.now());

        // Enviar a la empresa (desde variable de entorno)
        emailService.sendContactoEmail(empresaEmail, vars);
    }
}
