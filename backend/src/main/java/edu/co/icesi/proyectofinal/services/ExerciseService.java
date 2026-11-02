package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.Exercise;
import edu.co.icesi.proyectofinal.entity.User;

import java.util.List;

public interface ExerciseService {

    List<Exercise> findAll();

    List<Exercise> findVisibleTo(User user);

    boolean isVisibleTo(Exercise exercise, User user);

    Exercise getExerciseById(Integer id);

    Exercise saveExercise(Exercise exercise);

    Exercise updateExercise(Exercise exercise);

    void deleteExercise(Integer id);

    List<Exercise> findByType(String type);

    List<Exercise> findByDifficulty(String difficulty);

    List<Exercise> findPredefined();

    List<Exercise> findByUserId(Integer userId);
}