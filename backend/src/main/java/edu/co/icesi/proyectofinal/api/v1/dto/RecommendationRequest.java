package edu.co.icesi.proyectofinal.api.v1.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RecommendationRequest {
    private Integer trainerId;
    private Integer userId;
    private String description;
    private LocalDate dateCreated;
}
