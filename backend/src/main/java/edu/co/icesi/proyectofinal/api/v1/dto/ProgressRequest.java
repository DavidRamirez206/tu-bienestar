package edu.co.icesi.proyectofinal.api.v1.dto;

import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ProgressRequest {
    private Integer userId;

    @NotNull(message = "debe seleccionar un ejercicio")
    private Integer exerciseId;

    private Integer routineId;
    private LocalDateTime dateLogged;

    @Min(value = 0, message = "no puede ser negativo")
    private Integer reps;

    @Min(value = 0, message = "no puede ser negativa")
    private Integer durationMin;

    @NotNull(message = "es obligatorio")
    @Min(value = 1, message = "debe estar entre 1 y 10")
    @Max(value = 10, message = "debe estar entre 1 y 10")
    private Integer effortLevel;

    @Min(value = 1, message = "debe ser al menos 1")
    private Integer setNumber;

    @Min(value = 0, message = "no puede ser negativo")
    private Integer weightKg;
}
