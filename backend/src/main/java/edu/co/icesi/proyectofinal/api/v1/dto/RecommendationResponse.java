package edu.co.icesi.proyectofinal.api.v1.dto;

import lombok.Data;
import java.time.LocalDate;

@Data
public class RecommendationResponse {
    private Integer idRecommendation;
    private Integer trainerId;
    private String trainerFirstName;
    private String trainerLastName;
    private Integer userId;
    private String userFirstName;
    private String userLastName;
    private String description;
    private LocalDate dateCreated;
}
