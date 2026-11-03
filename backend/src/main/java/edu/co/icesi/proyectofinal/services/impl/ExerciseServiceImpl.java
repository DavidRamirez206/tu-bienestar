package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.exception.ConflictException;
import edu.co.icesi.proyectofinal.repository.ProgressRepository;
import edu.co.icesi.proyectofinal.entity.Exercise;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.repository.ExerciseRepository;
import edu.co.icesi.proyectofinal.services.ExerciseService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ExerciseServiceImpl implements ExerciseService {

    private final ExerciseRepository exerciseRepository;
    private final ProgressRepository progressRepository;

    public ExerciseServiceImpl(ExerciseRepository exerciseRepository, ProgressRepository progressRepository) {
        this.exerciseRepository = exerciseRepository;
        this.progressRepository = progressRepository;
    }

    @Override
    public List<Exercise> findAll() {
        return exerciseRepository.findAll();
    }

    @Override
    public List<Exercise> findVisibleTo(User user) {
        return isAdmin(user) ? exerciseRepository.findAll()
                : exerciseRepository.findByPredefinedTrueOrUserExerciseIdUser(user.getIdUser());
    }

    @Override
    public boolean isVisibleTo(Exercise exercise, User user) {
        boolean isOwner = exercise.getUserExercise() != null
                && exercise.getUserExercise().getIdUser().equals(user.getIdUser());
        return exercise.isPredefined() || isOwner || isAdmin(user);
    }

    private boolean isAdmin(User user) {
        return user.getUserRoles() != null && user.getUserRoles().stream()
                .anyMatch(userRole -> "ADMIN".equals(userRole.getRole().getName()));
    }

    @Override
    public Exercise getExerciseById(Integer id) {
        return exerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Exercise with id " + id + " not found"));
    }

    @Override
    public Exercise saveExercise(Exercise exercise) {
        return exerciseRepository.save(exercise);
    }

    @Override
    public Exercise updateExercise(Exercise exercise) {
        Exercise existing = exerciseRepository.findById(exercise.getIdExercise())
                .orElseThrow(() -> new RuntimeException("Exercise with id " + exercise.getIdExercise() + " not found"));
        existing.setName(exercise.getName());
        existing.setType(exercise.getType());
        existing.setDescription(exercise.getDescription());
        existing.setDurationMin(exercise.getDurationMin());
        existing.setDifficulty(exercise.getDifficulty());
        existing.setVideoUrl(exercise.getVideoUrl());
        existing.setPredefined(exercise.isPredefined());
        return exerciseRepository.save(existing);
    }

    @Override
    public void deleteExercise(Integer id) {

        if(!exerciseRepository.existsById(id)){
            throw new RuntimeException("Exercise with id " + id +
                    " not found");
        }
        // El progreso guardado es historial del usuario: no se borra junto con el ejercicio.
        if (!progressRepository.findByExerciseIdExercise(id).isEmpty()) {
            throw new ConflictException("No se puede eliminar un ejercicio con progreso registrado");
        }
        exerciseRepository.deleteById(id);
    }

    @Override
    public List<Exercise> findByType(String type) {
        return exerciseRepository.findByType(type);
    }

    @Override
    public List<Exercise> findByDifficulty(String difficulty) {
        return exerciseRepository.findByDifficulty(difficulty);
    }

    @Override
    public List<Exercise> findPredefined() {
        return exerciseRepository.findByPredefined(true);
    }

    @Override
    public List<Exercise> findByUserId(Integer userId) {
        return exerciseRepository.findByUserExerciseIdUser(userId);
    }
}