package edu.co.icesi.proyectofinal.repository;


import edu.co.icesi.proyectofinal.entity.Exercise;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ExerciseRepository extends JpaRepository<Exercise, Integer> {

    List<Exercise> findByType(String type);

    List<Exercise> findByDifficulty(String difficulty);

    List<Exercise> findByPredefined(Boolean predefined);

    List<Exercise> findByUserExerciseIdUser(Integer userId);

    Optional<Exercise> findTopByOrderByDurationMinDesc();

    Integer countByDifficulty(String difficulty);

}
