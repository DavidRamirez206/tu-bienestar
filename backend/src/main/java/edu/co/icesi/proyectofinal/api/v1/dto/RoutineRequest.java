package edu.co.icesi.proyectofinal.api.v1.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RoutineRequest {
    @NotBlank(message = "El nombre es obligatorio")
    private String name;
    private String description;
    private LocalDate creationDate;
    
    @JsonProperty("isPredesigned")
    private boolean predesigned;
    
    private Integer userId;
}
