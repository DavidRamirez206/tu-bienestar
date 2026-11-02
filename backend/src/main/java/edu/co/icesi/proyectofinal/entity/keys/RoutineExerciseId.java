package edu.co.icesi.proyectofinal.entity.keys;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.Data;

@Embeddable
@Data
public class RoutineExerciseId {

    @Column(name = "id_routine")
    private Integer routineId;

    @Column(name = "id_exercise")
    private Integer exerciseId;
}
