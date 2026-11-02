package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.Progress;

import java.util.List;

public interface ProgressService {

    List<Progress> getProgress();

    Progress getProgress(Integer id);

    Progress createProgress(Progress progress);

    Progress updateProgress(Progress progress);

    void deleteProgress(Integer id);

    List<Progress> getByUserId(Integer userId);

    List<Progress> getByRoutineId(Integer routineId);

    List<Progress> getByExerciseId(Integer exerciseId);
}
