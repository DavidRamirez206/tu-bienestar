package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.Progress;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProgressRepository extends JpaRepository<Progress, Integer> {

    List<Progress> findByUserProgressIdUser(Integer userId);

    List<Progress> findByRoutineIdRoutine(Integer routineId);

    List<Progress> findByExerciseIdExercise(Integer exerciseId);

    Optional<Progress> findTopByUserProgressIdUserOrderByDateLoggedDesc(Integer userId);
}
