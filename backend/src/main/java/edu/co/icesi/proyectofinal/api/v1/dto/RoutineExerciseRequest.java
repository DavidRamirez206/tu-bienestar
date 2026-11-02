package edu.co.icesi.proyectofinal.api.v1.dto;

import lombok.Data;

@Data
public class RoutineExerciseRequest {
    private Integer routineId;
    private Integer exerciseId;
    private Integer sets;
    private Integer targetReps;
    private Integer exerciseOrder;
}
