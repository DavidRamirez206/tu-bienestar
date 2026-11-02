package edu.co.icesi.proyectofinal.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseResponse {
    private Integer idExercise;
    private String name;
    private String type;
    private String description;
    private Integer durationMin;
    private String difficulty;
    private String videoUrl;
    
    @JsonProperty("isPredefined")
    private boolean predefined;
    
    private Integer userId;
}
