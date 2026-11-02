package edu.co.icesi.proyectofinal.entity;

import edu.co.icesi.proyectofinal.entity.keys.RoutineExerciseId;
import jakarta.persistence.*;
import lombok.Data;

@Table(name = "routine_exercises")
@Entity
@Data
public class RoutineExercise {

    @EmbeddedId
    private RoutineExerciseId id;

    @ManyToOne
    @MapsId("routineId")
    @JoinColumn(name = "id_routine")
    private Routine routine;

    @ManyToOne
    @MapsId("exerciseId")
    @JoinColumn(name = "id_exercise")
    private Exercise exercise;

    private Integer sets;
    private Integer targetReps;
    private Integer exerciseOrder;
}
