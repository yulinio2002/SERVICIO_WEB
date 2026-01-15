package com.example.proyecto.email.events;

import com.example.proyecto.dto.ContactoRequestDTO;
import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class ContactoCreadoEvent extends ApplicationEvent {
    private ContactoRequestDTO contactoRequestDTO;

    public ContactoCreadoEvent(Object source, ContactoRequestDTO newContacto){
        super(source);
        this.contactoRequestDTO = newContacto;
    }
}
