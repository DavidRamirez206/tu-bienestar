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
public class AssignmentRequest {
    @NotNull(message = "El usuario es obligatorio")
    private Integer userId;
    @NotNull(message = "El entrenador es obligatorio")
    private Integer trainerId;
}
