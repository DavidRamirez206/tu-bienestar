package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.NotificationResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.NotificationMapper;
import edu.co.icesi.proyectofinal.services.NotificationService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.security.Principal;
import java.util.List;
import java.util.Map;

/**
 * HU15 · Notificaciones en tiempo real.
 * Las notificaciones nuevas llegan por WebSocket a /user/queue/notifications. Estos endpoints
 * recuperan las que llegaron mientras el usuario no estaba conectado y permiten gestionarlas.
 * Cada usuario solo ve y modifica las suyas.
 */
@RestController
@RequestMapping("/api/v1/notifications")
@Tag(name = "Notifications", description = "Notificaciones del usuario de la sesión")
public class NotificationRestController {

    private final NotificationService notificationService;
    private final NotificationMapper notificationMapper;

    public NotificationRestController(NotificationService notificationService, NotificationMapper notificationMapper) {
        this.notificationService = notificationService;
        this.notificationMapper = notificationMapper;
    }

    @GetMapping
    @Operation(summary = "Mis notificaciones, de la más reciente a la más antigua")
    @PreAuthorize("hasAuthority('LIST_NOTIFICATION')")
    public ResponseEntity<List<NotificationResponse>> getMine(@RequestParam(defaultValue = "false") boolean unreadOnly,
                                                              Principal principal) {
        return ResponseEntity.ok(notificationService.findMine(principal.getName(), unreadOnly).stream()
                .map(notificationMapper::toResponse)
                .toList());
    }

    @PatchMapping("/{id}/read")
    @Operation(summary = "Marcar una notificación como leída")
    @PreAuthorize("hasAuthority('LIST_NOTIFICATION')")
    public ResponseEntity<NotificationResponse> markAsRead(@PathVariable Integer id, Principal principal) {
        return ResponseEntity.ok(notificationMapper.toResponse(notificationService.markAsRead(id, principal.getName())));
    }

    @PatchMapping("/read-all")
    @Operation(summary = "Marcar todas mis notificaciones como leídas")
    @PreAuthorize("hasAuthority('LIST_NOTIFICATION')")
    public ResponseEntity<Map<String, Integer>> markAllAsRead(Principal principal) {
        return ResponseEntity.ok(Map.of("updated", notificationService.markAllAsRead(principal.getName())));
    }

    @DeleteMapping("/{id}")
    @Operation(summary = "Eliminar una de mis notificaciones")
    @PreAuthorize("hasAuthority('DELETE_NOTIFICATION')")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Principal principal) {
        notificationService.delete(id, principal.getName());
        return ResponseEntity.noContent().build();
    }
}
