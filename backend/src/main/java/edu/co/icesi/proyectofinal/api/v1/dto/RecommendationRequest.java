package edu.co.icesi.proyectofinal.api.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import java.time.LocalDate;

@Data
public class RecommendationRequest {
    private Integer trainerId;

    @NotNull(message = "Debes indicar el usuario que recibe la recomendacion")
    private Integer userId;

    @NotBlank(message = "La recomendacion no puede estar vacia")
    private String description;

    private LocalDate dateCreated;
}
