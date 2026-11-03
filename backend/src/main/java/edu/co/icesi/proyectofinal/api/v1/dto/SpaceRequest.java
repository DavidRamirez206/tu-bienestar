package edu.co.icesi.proyectofinal.api.v1.dto;

import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class SpaceRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    @NotNull(message = "La capacidad es obligatoria")
    @Min(value = 1, message = "La capacidad debe ser mayor que cero")
    private Integer capacity;
    @NotBlank(message = "La ubicación es obligatoria")
    private String location;
}
