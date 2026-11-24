package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.Exercise;
import edu.co.icesi.proyectofinal.entity.Routine;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.repository.ExerciseRepository;
import edu.co.icesi.proyectofinal.repository.RoutineRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.services.RoutineService;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class RoutineServiceIntegrationTest {

    @Autowired
    private RoutineService routineService;

    @Autowired
    private RoutineRepository routineRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ExerciseRepository exerciseRepository;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setFirstName("Felipe");
        user.setLastName("Ossa");
        user.setInstitutionalEmail("felipe.ossa@icesi.edu.co");
        user.setPassword("pass");
        userRepository.save(user);
    }

    private Routine buildRoutine(String name, boolean predesigned) {
        Routine r = new Routine();
        r.setName(name);
        r.setDescription("Descripción");
        r.setCreationDate(LocalDate.now());
        r.setPredesigned(predesigned);
        r.setUserRoutine(user);
        r.setRoutineExercises(new ArrayList<>());
        return r;
    }

    @Test
    void save_shouldPersist() {
        Routine saved = routineService.save(buildRoutine("Rutina A", false));

        assertNotNull(saved.getIdRoutine());
    }

    @Test
    void findById_whenExists_shouldReturn() {
        Routine saved = routineRepository.save(buildRoutine("Rutina B", false));

        Routine result = routineService.findById(saved.getIdRoutine());

        assertEquals("Rutina B", result.getName());
    }

    @Test
    void findById_whenNotExists_shouldThrowRuntimeException() {
        assertThrows(RuntimeException.class, () -> routineService.findById(999));
    }

    @Test
    void update_shouldReflectChanges() {
        Routine saved = routineRepository.save(buildRoutine("Vieja", false));
        saved.setName("Nueva");
        saved.setPredesigned(true);

        Routine result = routineService.update(saved.getIdRoutine(), saved);

        assertEquals("Nueva", result.getName());
        assertTrue(result.isPredesigned());
    }

    @Test
    void delete_shouldRemove() {
        Routine saved = routineRepository.save(buildRoutine("A borrar", false));

        routineService.delete(saved.getIdRoutine());

        assertTrue(routineRepository.findAll().isEmpty());
    }

    @Test
    void findAll_shouldReturnAll() {
        routineRepository.save(buildRoutine("R1", false));
        routineRepository.save(buildRoutine("R2", true));

        assertEquals(2, routineService.findAll().size());
    }

    @Test
    void findByUserId_shouldReturnFiltered() {
        routineRepository.save(buildRoutine("R1", false));

        List<Routine> result = routineService.findByUserId(user.getIdUser());

        assertEquals(1, result.size());
    }

    @Test
    void findPredesigned_shouldReturnOnlyPredesigned() {
        routineRepository.save(buildRoutine("Normal", false));
        routineRepository.save(buildRoutine("Prediseñada", true));

        List<Routine> result = routineService.findPredesigned();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isPredesigned());
    }

    @Test
    void assignExercises_shouldLinkExercisesToRoutine() {
        Routine saved = routineRepository.save(buildRoutine("Rutina con ejercicios", false));

        Exercise ex = new Exercise();
        ex.setName("Sentadilla");
        ex.setType("fuerza");
        ex.setDifficulty("medio");
        ex.setDurationMin(10);
        ex.setPredefined(true);
        exerciseRepository.save(ex);

        routineService.assignExercises(saved.getIdRoutine(), List.of(ex.getIdExercise()));

        Routine updated = routineRepository.findById(saved.getIdRoutine()).orElseThrow();
        assertEquals(1, updated.getRoutineExercises().size());
    }

}