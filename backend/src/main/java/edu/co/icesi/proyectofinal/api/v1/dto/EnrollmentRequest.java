package edu.co.icesi.proyectofinal.api.v1.dto;

import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class EnrollmentRequest {
    /** Opcional: si no llega se inscribe quien hace la peticion. */
    private Integer userId;
    @NotNull(message = "La actividad es obligatoria")
    private Integer activityId;
}
