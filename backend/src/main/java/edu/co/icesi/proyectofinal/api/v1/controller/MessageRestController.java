package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.ContactResponse;
import edu.co.icesi.proyectofinal.api.v1.dto.MessageRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.MessageResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.MessageMapper;
import edu.co.icesi.proyectofinal.services.MessageService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;

/**
 * HU16 · Mensajes y alertas.
 * Los mensajes nuevos llegan por WebSocket a /user/queue/messages. Estos endpoints envían mensajes
 * y recuperan la conversación guardada. Leer los propios mensajes solo exige tener sesión
 * (VIEW_OWN_PROFILE), porque el rol TRAINEE no tiene LIST_MESSAGE y debe poder leer lo que recibe.
 */
@RestController
@RequestMapping("/api/v1/messages")
@Tag(name = "Messages", description = "Mensajes entre entrenadores y usuarios asignados")
public class MessageRestController {

    private final MessageService messageService;
    private final MessageMapper messageMapper;

    public MessageRestController(MessageService messageService, MessageMapper messageMapper) {
        this.messageService = messageService;
        this.messageMapper = messageMapper;
    }

    @GetMapping
    @Operation(summary = "Mensajes que envié o recibí, del más antiguo al más reciente")
    @PreAuthorize("hasAnyAuthority('LIST_MESSAGE', 'VIEW_OWN_PROFILE')")
    public ResponseEntity<List<MessageResponse>> getMine(Principal principal) {
        return ResponseEntity.ok(messageService.findMine(principal.getName()).stream()
                .map(messageMapper::toResponse)
                .toList());
    }

    @GetMapping("/contacts")
    @Operation(summary = "Personas con las que puedo conversar (mis asignados o mis entrenadores)")
    @PreAuthorize("hasAnyAuthority('LIST_MESSAGE', 'VIEW_OWN_PROFILE')")
    public ResponseEntity<List<ContactResponse>> getContacts(Principal principal) {
        return ResponseEntity.ok(messageService.contacts(principal.getName()));
    }

    @PostMapping
    @Operation(summary = "Enviar un mensaje a uno o varios usuarios asignados")
    @PreAuthorize("hasAuthority('CREATE_MESSAGE')")
    public ResponseEntity<List<MessageResponse>> send(@Valid @RequestBody MessageRequest request, Principal principal) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(messageService.send(principal.getName(), request.getReceiverIds(), request.getContent()).stream()
                        .map(messageMapper::toResponse)
                        .toList());
    }
}
