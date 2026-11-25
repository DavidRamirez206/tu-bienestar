package edu.co.icesi.proyectofinal.api.v1.dto;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Cuerpo para activar o desactivar la cuenta de un usuario (HU9.1). */
@Data
public class UserActiveRequest {

    @NotNull(message = "Debes indicar si la cuenta queda activa o inactiva")
    private Boolean active;
}
