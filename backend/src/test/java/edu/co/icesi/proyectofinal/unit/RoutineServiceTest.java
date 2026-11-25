package edu.co.icesi.proyectofinal.unit;

import edu.co.icesi.proyectofinal.entity.Exercise;
import edu.co.icesi.proyectofinal.entity.Routine;
import edu.co.icesi.proyectofinal.entity.RoutineExercise;
import edu.co.icesi.proyectofinal.repository.ExerciseRepository;
import edu.co.icesi.proyectofinal.repository.RoutineRepository;
import edu.co.icesi.proyectofinal.services.impl.RoutineServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoutineServiceTest {

    @Mock
    private RoutineRepository routineRepository;

    @Mock
    private ExerciseRepository exerciseRepository;

    @InjectMocks
    private RoutineServiceImpl routineService;

    private Routine routine;

    @BeforeEach
    void setUp() {
        routine = new Routine();
        routine.setIdRoutine(1);
        routine.setName("Rutina A");
        routine.setDescription("Descripcion");
        routine.setCreationDate(LocalDate.now());
        routine.setPredesigned(false);
        routine.setRoutineExercises(new ArrayList<>());
    }

    @Test
    void findAll_shouldReturnList() {
        when(routineRepository.findAll()).thenReturn(List.of(routine));

        assertEquals(1, routineService.findAll().size());
    }

    @Test
    void findById_whenExists_shouldReturn() {
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));

        Routine result = routineService.findById(1);

        assertNotNull(result);
        assertEquals("Rutina A", result.getName());
    }

    @Test
    void findById_whenNotExists_shouldThrowRuntimeException() {
        when(routineRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> routineService.findById(99));
    }

    @Test
    void save_shouldReturnSaved() {
        when(routineRepository.save(routine)).thenReturn(routine);

        Routine result = routineService.save(routine);

        assertNotNull(result);
        assertEquals("Rutina A", result.getName());
    }

    @Test
    void delete_shouldCallRepository() {
        when(routineRepository.existsById(1)).thenReturn(true);
        doNothing().when(routineRepository).deleteById(1);
        routineService.delete(1);
        verify(routineRepository).deleteById(1);
    }

    @Test
    void update_whenExists_shouldUpdateAndReturn() {
        Routine updated = new Routine();
        updated.setName("Rutina B");
        updated.setDescription("Nueva descripcion");
        updated.setCreationDate(LocalDate.now());
        updated.setPredesigned(true);

        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(routineRepository.save(any(Routine.class))).thenAnswer(x -> x.getArgument(0));

        Routine result = routineService.update(1, updated);

        assertEquals("Rutina B", result.getName());
        assertTrue(result.isPredesigned());
    }

    @Test
    void update_whenNotExists_shouldThrowRuntimeException() {
        when(routineRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> routineService.update(99, routine));
    }

    @Test
    void assignExercises_shouldAddExercisesToRoutine() {
        Exercise exercise = new Exercise();
        exercise.setIdExercise(10);
        exercise.setName("Sentadilla");

        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(exerciseRepository.findById(10)).thenReturn(Optional.of(exercise));
        when(routineRepository.save(any(Routine.class))).thenReturn(routine);

        routineService.assignExercises(1, List.of(10));

        assertEquals(1, routine.getRoutineExercises().size());
        RoutineExercise re = routine.getRoutineExercises().get(0);
        assertEquals(1, re.getExerciseOrder());
    }

    @Test
    void assignExercises_whenRoutineNotFound_shouldThrowRuntimeException() {
        when(routineRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> routineService.assignExercises(99, List.of(1)));
    }

    @Test
    void assignExercises_whenExerciseNotFound_shouldThrowRuntimeException() {
        when(routineRepository.findById(1)).thenReturn(Optional.of(routine));
        when(exerciseRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> routineService.assignExercises(1, List.of(99)));
    }

    @Test
    void findByUserId_shouldReturnFiltered() {
        when(routineRepository.findByUserRoutineIdUser(1)).thenReturn(List.of(routine));

        assertEquals(1, routineService.findByUserId(1).size());
    }

    @Test
    void findPredesigned_shouldReturnOnlyPredesigned() {
        routine.setPredesigned(true);
        when(routineRepository.findByPredesigned(true)).thenReturn(List.of(routine));

        List<Routine> result = routineService.findPredesigned();

        assertEquals(1, result.size());
        assertTrue(result.get(0).isPredesigned());
    }
}