package com.example.proyecto.email.listeners;

import com.example.proyecto.email.events.ContactoCreadoEvent;
import com.example.proyecto.email.service.EmailService;
import com.example.proyecto.exception.AsyncOperationInterruptedException;
import lombok.RequiredArgsConstructor;
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
        vars.put("nameCliente", contactoCreadoEvent.getContactoRequestDTO().getNombre());
        vars.put("nameProveedor", contactoCreadoEvent.getContactoRequestDTO().getEmpresa());
        vars.put("fecha", LocalDateTime.now());
        vars.put("direccion", contactoCreadoEvent.getContactoRequestDTO().getCorreo());
        vars.put("nombreServicio", contactoCreadoEvent.getContactoRequestDTO().getMensaje());
        emailService.sendCreateReservaEmail(contactoCreadoEvent.getContactoRequestDTO().getCorreo(), vars);
    }
}
