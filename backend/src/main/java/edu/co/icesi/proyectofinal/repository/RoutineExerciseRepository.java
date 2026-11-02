package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.RoutineExercise;
import edu.co.icesi.proyectofinal.entity.keys.RoutineExerciseId;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RoutineExerciseRepository extends JpaRepository<RoutineExercise, RoutineExerciseId> {

    List<RoutineExercise> findByRoutineIdRoutine(Integer routineId);

    List<RoutineExercise> findByExerciseIdExercise(Integer exerciseId);

    Optional<RoutineExercise> findTopByRoutineIdRoutineOrderByExerciseOrderAsc(Integer routineId);

    long countByRoutineIdRoutine(Integer routineId);
}
