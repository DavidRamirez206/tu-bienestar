package edu.co.icesi.proyectofinal.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class NotificationResponse {
    private Integer idNotification;
    private Integer userTargetId;
    private Integer userSourceId;
    private String userSourceName;
    /** NEW_ACTIVITY, NEW_SPACE o NEW_MESSAGE */
    private String type;
    private String message;
    private Integer referenceId;
    /** ACTIVITY, SPACE o MESSAGE: indica a que detalle lleva la notificacion */
    private String referenceType;
    private LocalDateTime dateSent;
    @JsonProperty("isRead")
    private boolean read;
}
