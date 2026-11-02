package edu.co.icesi.proyectofinal.api.v1.dto;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class ProgressRequest {
    private Integer userId;
    private Integer exerciseId;
    private Integer routineId;
    private LocalDateTime dateLogged;
    private Integer reps;
    private Integer durationMin;
    private Integer effortLevel;
    private Integer setNumber;
    private Integer weightKg;
}
