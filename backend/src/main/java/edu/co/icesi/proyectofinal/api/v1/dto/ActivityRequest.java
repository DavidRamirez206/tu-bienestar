package edu.co.icesi.proyectofinal.api.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ActivityRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    @NotBlank(message = "La descripción es obligatoria")
    private String description;
    @NotNull(message = "La fecha de inicio es obligatoria")
    private LocalDate startDate;
    @NotNull(message = "La fecha de fin es obligatoria")
    private LocalDate endDate;
    @NotNull(message = "El lugar es obligatorio")
    private Integer spaceId;
}
