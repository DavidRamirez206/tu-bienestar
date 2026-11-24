package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.Exercise;
import edu.co.icesi.proyectofinal.entity.Routine;
import edu.co.icesi.proyectofinal.entity.RoutineExercise;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.entity.keys.RoutineExerciseId;
import edu.co.icesi.proyectofinal.repository.ExerciseRepository;
import edu.co.icesi.proyectofinal.repository.RoutineExerciseRepository;
import edu.co.icesi.proyectofinal.repository.RoutineRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.services.RoutineExerciseService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class RoutineExerciseServiceIntegrationTest {

    @Autowired
    private RoutineExerciseService routineExerciseService;

    @Autowired
    private RoutineExerciseRepository routineExerciseRepository;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    @Autowired
    private UserRepository userRepository;

    private Routine routine;
    private Exercise exercise;
    private RoutineExerciseId id;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setFirstName("Juan");
        user.setLastName("Test");
        user.setInstitutionalEmail("juan.test@icesi.edu.co");
        user.setPassword("pass");
        userRepository.save(user);

        routine = new Routine();
        routine.setName("Rutina Test");
        routine.setDescription("Desc");
        routine.setCreationDate(LocalDate.now());
        routine.setPredesigned(false);
        routine.setUserRoutine(user);
        routine.setRoutineExercises(new ArrayList<>());
        routineRepository.save(routine);

        exercise = new Exercise();
        exercise.setName("Sentadilla");
        exercise.setType("fuerza");
        exercise.setDifficulty("medio");
        exercise.setDurationMin(10);
        exercise.setPredefined(true);
        exerciseRepository.save(exercise);

        id = new RoutineExerciseId();
        id.setRoutineId(routine.getIdRoutine());
        id.setExerciseId(exercise.getIdExercise());
    }

    private RoutineExercise buildRoutineExercise(int sets, int reps, int order) {
        RoutineExercise re = new RoutineExercise();
        re.setId(id);
        re.setRoutine(routine);
        re.setExercise(exercise);
        re.setSets(sets);
        re.setTargetReps(reps);
        re.setExerciseOrder(order);
        return re;
    }

    @Test
    void save_shouldPersist() {
        RoutineExercise saved = routineExerciseService.save(buildRoutineExercise(3, 12, 1));

        assertNotNull(saved.getId());
        assertEquals(1, routineExerciseRepository.findAll().size());
    }

    @Test
    void getById_whenExists_shouldReturn() {
        routineExerciseRepository.save(buildRoutineExercise(3, 12, 1));

        RoutineExercise result = routineExerciseService.getById(id);

        assertEquals(id, result.getId());
        assertEquals(3, result.getSets());
        assertEquals(12, result.getTargetReps());
    }

    @Test
    void getById_whenNotExists_shouldThrowRuntimeException() {
        RoutineExerciseId missing = new RoutineExerciseId();
        missing.setRoutineId(999);
        missing.setExerciseId(999);

        assertThrows(RuntimeException.class,
                () -> routineExerciseService.getById(missing));
    }

    @Test
    void deleteById_shouldRemove() {
        routineExerciseRepository.save(buildRoutineExercise(3, 12, 1));

        routineExerciseService.deleteById(id);

        assertTrue(routineExerciseRepository.findAll().isEmpty());
    }

    @Test
    void findAll_shouldReturnAll() {
        routineExerciseRepository.save(buildRoutineExercise(3, 12, 1));

        assertEquals(1, routineExerciseService.findAll().size());
    }

    @Test
    void getByRoutineId_shouldReturnFiltered() {
        routineExerciseRepository.save(buildRoutineExercise(3, 12, 1));

        List<RoutineExercise> result =
                routineExerciseService.getByRoutineId(routine.getIdRoutine());

        assertEquals(1, result.size());
    }

    @Test
    void getByExerciseId_shouldReturnFiltered() {
        routineExerciseRepository.save(buildRoutineExercise(3, 12, 1));

        List<RoutineExercise> result =
                routineExerciseService.getByExerciseId(exercise.getIdExercise());

        assertEquals(1, result.size());
    }

    @Test
    void update_shouldReflectChangesInDB() {
        routineExerciseRepository.save(buildRoutineExercise(3, 12, 1));

        RoutineExercise updated = new RoutineExercise();
        updated.setSets(5);
        updated.setTargetReps(20);
        updated.setExerciseOrder(2);

        RoutineExercise result = routineExerciseService.update(id, updated);

        assertEquals(5, result.getSets());
        assertEquals(20, result.getTargetReps());
        assertEquals(2, result.getExerciseOrder());

        // Verificar que persiste en BD
        RoutineExercise fromDb = routineExerciseRepository.findById(id).orElseThrow();
        assertEquals(5, fromDb.getSets());
        assertEquals(20, fromDb.getTargetReps());
    }

    @Test
    void update_whenNotExists_shouldThrowRuntimeException() {
        RoutineExerciseId missing = new RoutineExerciseId();
        missing.setRoutineId(999);
        missing.setExerciseId(999);

        assertThrows(RuntimeException.class,
                () -> routineExerciseService.update(missing, new RoutineExercise()));
    }

    @Test
    void countByRoutineId_shouldReturnCorrectCount() {
        routineExerciseRepository.save(buildRoutineExercise(3, 12, 1));

        long count = routineExerciseService.countByRoutineId(routine.getIdRoutine());

        assertEquals(1L, count);
    }

    @Test
    void countByRoutineId_whenNone_shouldReturnZero() {
        assertEquals(0L, routineExerciseService.countByRoutineId(999));
    }
}