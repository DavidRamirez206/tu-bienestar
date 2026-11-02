package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.Exercise;
import edu.co.icesi.proyectofinal.entity.Routine;
import edu.co.icesi.proyectofinal.entity.RoutineExercise;
import edu.co.icesi.proyectofinal.entity.keys.RoutineExerciseId;
import edu.co.icesi.proyectofinal.repository.ExerciseRepository;
import edu.co.icesi.proyectofinal.repository.RoutineExerciseRepository;
import edu.co.icesi.proyectofinal.repository.RoutineRepository;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.services.ExerciseService;
import edu.co.icesi.proyectofinal.services.RoutineExerciseService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
public class RoutineExerciseServiceImpl implements RoutineExerciseService {

    private final RoutineExerciseRepository routineExerciseRepository;
    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final ExerciseService exerciseService;

    public RoutineExerciseServiceImpl(RoutineExerciseRepository routineExerciseRepository, 
                                     RoutineRepository routineRepository,
                                     ExerciseRepository exerciseRepository,
                                     ExerciseService exerciseService) {
        this.routineExerciseRepository = routineExerciseRepository;
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.exerciseService = exerciseService;
    }

    @Override
    public RoutineExercise save(RoutineExercise routineExercise) {
        return routineExerciseRepository.save(routineExercise);
    }

    @Override
    public void deleteById(RoutineExerciseId id) {

        if(!routineExerciseRepository.existsById(id)){
            throw new RuntimeException("Theres no routine associated with id " + id +
                    " not found");
        }

        routineExerciseRepository.deleteById(id);
    }

    @Override
    public RoutineExercise getById(RoutineExerciseId id) {
        return routineExerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "RoutineExercise with routineId " + id.getRoutineId()
                                + " and exerciseId " + id.getExerciseId() + " not found"));
    }

    @Override
    public List<RoutineExercise> findAll() {
        return routineExerciseRepository.findAll();
    }

    @Override
    public List<RoutineExercise> getByRoutineId(Integer routineId) {
        return routineExerciseRepository.findByRoutineIdRoutine(routineId);
    }

    @Override
    public List<RoutineExercise> getByExerciseId(Integer exerciseId) {
        return routineExerciseRepository.findByExerciseIdExercise(exerciseId);
    }

    @Override
    public RoutineExercise update(RoutineExerciseId id, RoutineExercise updated) {
        RoutineExercise existing = routineExerciseRepository.findById(id)
                .orElseThrow(() -> new RuntimeException(
                        "RoutineExercise with routineId " + id.getRoutineId()
                                + " and exerciseId " + id.getExerciseId() + " not found"));
        existing.setSets(updated.getSets());
        existing.setTargetReps(updated.getTargetReps());
        existing.setExerciseOrder(updated.getExerciseOrder());
        return routineExerciseRepository.save(existing);
    }

    @Override
    public long countByRoutineId(Integer routineId) {
        return routineExerciseRepository.countByRoutineIdRoutine(routineId);
    }

    @Override
    @Transactional
    public List<RoutineExercise> assignExercisesToRoutine(Integer routineId, List<Integer> exerciseIds) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Routine not found with id: " + routineId));

        List<RoutineExercise> createdAssignments = new ArrayList<>();
        int order = 1;

        for (Integer exerciseId : exerciseIds) {
            Exercise exercise = exerciseRepository.findById(exerciseId)
                    .orElseThrow(() -> new RuntimeException("Exercise not found with id: " + exerciseId));

            RoutineExerciseId id = new RoutineExerciseId();
            id.setRoutineId(routineId);
            id.setExerciseId(exerciseId);

            RoutineExercise re = new RoutineExercise();
            re.setId(id);
            re.setRoutine(routine);
            re.setExercise(exercise);
            re.setSets(3); // Default values
            re.setTargetReps(10);
            re.setExerciseOrder(order++);

            createdAssignments.add(routineExerciseRepository.save(re));
        }

        return createdAssignments;
    }

    @Override
    @Transactional
    public List<RoutineExercise> replaceExercises(Integer routineId, List<RoutineExercise> items, User user) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Routine not found with id: " + routineId));
        if (routine.getRoutineExercises() == null) {
            routine.setRoutineExercises(new ArrayList<>());
        }

        Map<Integer, RoutineExercise> current = new HashMap<>();
        routine.getRoutineExercises().forEach(item -> current.put(item.getId().getExerciseId(), item));

        Set<Integer> incomingIds = new HashSet<>();
        for (RoutineExercise item : items) {
            Integer exerciseId = item.getId().getExerciseId();
            if (!incomingIds.add(exerciseId)) {
                throw new IllegalArgumentException("Invalid routine: duplicated exercise " + exerciseId);
            }
            if (item.getSets() == null || item.getSets() < 1
                    || item.getTargetReps() == null || item.getTargetReps() < 1) {
                throw new IllegalArgumentException("Invalid routine: sets and repetitions must be greater than zero");
            }
            if (!current.containsKey(exerciseId) && !exerciseService.isVisibleTo(exerciseService.getExerciseById(exerciseId), user)) {
                throw new AccessDeniedException("You cannot use this exercise");
            }
        }

        // Se actualiza en sitio lo que ya existe: borrar y reinsertar la misma clave en una
        // transaccion viola la llave primaria, porque Hibernate ejecuta los INSERT primero.
        routine.getRoutineExercises().removeIf(item -> !incomingIds.contains(item.getId().getExerciseId()));

        int position = 1;
        for (RoutineExercise item : items) {
            Integer exerciseId = item.getId().getExerciseId();
            RoutineExercise target = current.get(exerciseId);
            if (target == null) {
                RoutineExerciseId id = new RoutineExerciseId();
                id.setRoutineId(routineId);
                id.setExerciseId(exerciseId);
                target = new RoutineExercise();
                target.setId(id);
                target.setRoutine(routine);
                target.setExercise(exerciseService.getExerciseById(exerciseId));
                routine.getRoutineExercises().add(target);
            }
            target.setSets(item.getSets());
            target.setTargetReps(item.getTargetReps());
            target.setExerciseOrder(item.getExerciseOrder() != null ? item.getExerciseOrder() : position);
            position++;
        }

        return new ArrayList<>(routineRepository.save(routine).getRoutineExercises());
    }
}
