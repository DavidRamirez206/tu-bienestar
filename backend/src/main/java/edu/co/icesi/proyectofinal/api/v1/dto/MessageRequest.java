package edu.co.icesi.proyectofinal.api.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.List;

/** El remitente es siempre el usuario de la sesion. */
@Data
public class MessageRequest {

    @NotEmpty(message = "Elige al menos un destinatario")
    private List<@NotNull Integer> receiverIds;

    @NotBlank(message = "El mensaje no puede estar vacío")
    @Size(max = 255, message = "El mensaje admite máximo 255 caracteres")
    private String content;
}
