package edu.co.icesi.proyectofinal.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ExerciseRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    @NotBlank(message = "El tipo es obligatorio")
    private String type;
    private String description;
    @Min(value = 1, message = "La duración debe ser mayor que cero")
    private Integer durationMin;
    private String difficulty;
    private String videoUrl;
    
    @JsonProperty("isPredefined")
    private boolean predefined;
    
    private Integer userId;
}
