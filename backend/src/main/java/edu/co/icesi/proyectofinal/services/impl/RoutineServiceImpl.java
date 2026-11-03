package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.Exercise;
import edu.co.icesi.proyectofinal.entity.Routine;
import edu.co.icesi.proyectofinal.entity.RoutineExercise;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.entity.keys.RoutineExerciseId;
import edu.co.icesi.proyectofinal.repository.ExerciseRepository;
import edu.co.icesi.proyectofinal.repository.RoutineRepository;
import edu.co.icesi.proyectofinal.services.RoutineService;
import edu.co.icesi.proyectofinal.repository.ProgressRepository;
import jakarta.transaction.Transactional;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class RoutineServiceImpl implements RoutineService {

    private final RoutineRepository routineRepository;
    private final ExerciseRepository exerciseRepository;
    private final ProgressRepository progressRepository;

    public RoutineServiceImpl(RoutineRepository routineRepository,
                              ExerciseRepository exerciseRepository,
                              ProgressRepository progressRepository) {
        this.routineRepository = routineRepository;
        this.exerciseRepository = exerciseRepository;
        this.progressRepository = progressRepository;
    }

    @Override
    @Transactional
    public void assignExercises(Integer routineId, List<Integer> exerciseIds) {
        Routine routine = routineRepository.findById(routineId)
                .orElseThrow(() -> new RuntimeException("Routine with id " + routineId + " not found"));

        routine.getRoutineExercises().clear();

        if (exerciseIds != null) {
            int order = 1;
            for (Integer exerciseId : exerciseIds) {
                Exercise exercise = exerciseRepository.findById(exerciseId)
                        .orElseThrow(() -> new RuntimeException("Exercise with id " + exerciseId + " not found"));

                RoutineExerciseId reId = new RoutineExerciseId();
                reId.setRoutineId(routineId);
                reId.setExerciseId(exerciseId);

                RoutineExercise re = new RoutineExercise();
                re.setId(reId);
                re.setRoutine(routine);
                re.setExercise(exercise);
                re.setExerciseOrder(order++);

                routine.getRoutineExercises().add(re);
            }
        }

        routineRepository.save(routine);
    }

    @Override
    public List<Routine> findAll() {
        return routineRepository.findAll();
    }

    @Override
    public Routine findById(Integer id) {
        return routineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Routine with id " + id + " not found"));
    }

    @Override
    public Routine save(Routine routine) {

        if(routine.getCreationDate() == null){
            routine.setCreationDate(LocalDate.now());
        }

        return routineRepository.save(routine);
    }

    @Override
    @Transactional
    public void delete(Integer id) {

        if(!routineRepository.existsById(id)){
            throw new RuntimeException("Routine with id " + id +
                    " not found");
        }

        // El progreso es historial del usuario: se conserva aunque la rutina desaparezca.
        progressRepository.findByRoutineIdRoutine(id).forEach(progress -> progress.setRoutine(null));

        routineRepository.deleteById(id);
    }

    @Override
    public Routine update(Integer id, Routine updated) {
        Routine existing = routineRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Routine with id " + id + " not found"));
        existing.setName(updated.getName());
        existing.setDescription(updated.getDescription());
        existing.setPredesigned(updated.isPredesigned());
        existing.setLevel(updated.getLevel());
        if (updated.getCreationDate() != null) {
            existing.setCreationDate(updated.getCreationDate());
        }

        return routineRepository.save(existing);
    }

    @Override
    public List<Routine> findByUserId(Integer userId) {
        return routineRepository.findByUserRoutineIdUser(userId);
    }

    @Override
    public List<Routine> findPredesigned() {
        return routineRepository.findByPredesigned(true);
    }

    @Override
    @Transactional
    public Routine adoptRoutine(Integer routineId, User targetUser) {
        Routine source = findById(routineId);
        if (!source.isPredesigned()) {
            throw new IllegalArgumentException("Invalid routine: only predesigned routines can be adopted");
        }

        String adoptedName = source.getName() + " (Adoptada)";
        if (routineRepository.existsByNameAndUserRoutineIdUser(adoptedName, targetUser.getIdUser())) {
            throw new IllegalArgumentException("Adopted routine already exists");
        }

        Routine copy = new Routine();
        copy.setName(adoptedName);
        copy.setDescription(source.getDescription());
        copy.setLevel(source.getLevel());
        copy.setCreationDate(LocalDate.now());
        copy.setPredesigned(false);
        copy.setUserRoutine(targetUser);
        copy.setRoutineExercises(new ArrayList<>());

        Routine savedCopy = routineRepository.save(copy);

        for (RoutineExercise item : source.getRoutineExercises() == null ? List.<RoutineExercise>of() : source.getRoutineExercises()) {
            RoutineExerciseId id = new RoutineExerciseId();
            id.setRoutineId(savedCopy.getIdRoutine());
            id.setExerciseId(item.getExercise().getIdExercise());

            RoutineExercise itemCopy = new RoutineExercise();
            itemCopy.setId(id);
            itemCopy.setRoutine(savedCopy);
            itemCopy.setExercise(item.getExercise());
            itemCopy.setSets(item.getSets());
            itemCopy.setTargetReps(item.getTargetReps());
            itemCopy.setExerciseOrder(item.getExerciseOrder());

            savedCopy.getRoutineExercises().add(itemCopy);
        }

        return routineRepository.save(savedCopy);
    }

    @Override
    public boolean canView(Routine routine, User user) {
        return routine.isPredesigned() || isOwner(routine, user) || isAdmin(user);
    }

    @Override
    public void assertCanModify(Integer routineId, User user) {
        Routine routine = findById(routineId);
        if (!isOwner(routine, user) && !isAdmin(user)) {
            throw new AccessDeniedException("You can only modify your own routines");
        }
    }

    private boolean isOwner(Routine routine, User user) {
        return routine.getUserRoutine() != null
                && routine.getUserRoutine().getIdUser().equals(user.getIdUser());
    }

    private boolean isAdmin(User user) {
        return user.getUserRoles() != null && user.getUserRoles().stream()
                .anyMatch(userRole -> "ADMIN".equals(userRole.getRole().getName()));
    }
}
