package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.Progress;
import edu.co.icesi.proyectofinal.repository.ProgressRepository;
import edu.co.icesi.proyectofinal.services.ProgressService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class ProgressServiceImpl implements ProgressService {

    private final ProgressRepository progressRepository;

    public ProgressServiceImpl(ProgressRepository progressRepository) {
        this.progressRepository = progressRepository;
    }

    @Override
    public List<Progress> getProgress() {
        return progressRepository.findAll();
    }

    @Override
    public Progress getProgress(Integer id) {
        return progressRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Progress with id " + id + " not found"));
    }

    @Override
    public Progress createProgress(Progress progress) {

        if (progress.getDateLogged() == null) {
            progress.setDateLogged(LocalDateTime.now());
        }
        return progressRepository.save(progress);
    }

    @Override
    public Progress updateProgress(Progress progress) {
        Progress existing = progressRepository.findById(progress.getIdProgress())
                .orElseThrow(() -> new RuntimeException("Progress with id " + progress.getIdProgress() + " not found"));
        existing.setDateLogged(progress.getDateLogged());
        existing.setReps(progress.getReps());
        existing.setDurationMin(progress.getDurationMin());
        existing.setEffortLevel(progress.getEffortLevel());
        existing.setSetNumber(progress.getSetNumber());
        existing.setWeightKg(progress.getWeightKg());
        existing.setExercise(progress.getExercise());
        existing.setRoutine(progress.getRoutine());

        if (progress.getDateLogged() == null) {
            existing.setDateLogged(LocalDateTime.now());
        }else{
            existing.setDateLogged(progress.getDateLogged());
        }

        return progressRepository.save(existing);
    }

    @Override
    public void deleteProgress(Integer id) {

        if(!progressRepository.existsById(id)){
            throw new RuntimeException("Progress with id " + id +
                    " not found");
        }
        progressRepository.deleteById(id);
    }

    @Override
    public List<Progress> getByUserId(Integer userId) {
        return progressRepository.findByUserProgressIdUser(userId);
    }

    @Override
    public List<Progress> getByRoutineId(Integer routineId) {
        return progressRepository.findByRoutineIdRoutine(routineId);
    }

    @Override
    public List<Progress> getByExerciseId(Integer exerciseId) {
        return progressRepository.findByExerciseIdExercise(exerciseId);
    }
}