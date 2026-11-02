package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.RoutineExercise;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.entity.keys.RoutineExerciseId;

import java.util.List;

public interface RoutineExerciseService {

    RoutineExercise save(RoutineExercise routineExercise);

    void deleteById(RoutineExerciseId id);

    RoutineExercise getById(RoutineExerciseId id);

    List<RoutineExercise> findAll();

    List<RoutineExercise> getByRoutineId(Integer routineId);

    List<RoutineExercise> getByExerciseId(Integer exerciseId);

    RoutineExercise update(RoutineExerciseId id, RoutineExercise updated);

    long countByRoutineId(Integer routineId);

    List<RoutineExercise> assignExercisesToRoutine(Integer routineId, List<Integer> exerciseIds);

    List<RoutineExercise> replaceExercises(Integer routineId, List<RoutineExercise> items, User user);
}