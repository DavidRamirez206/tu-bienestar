package edu.co.icesi.proyectofinal.unit;

import edu.co.icesi.proyectofinal.entity.Exercise;
import edu.co.icesi.proyectofinal.entity.Routine;
import edu.co.icesi.proyectofinal.entity.RoutineExercise;
import edu.co.icesi.proyectofinal.entity.keys.RoutineExerciseId;
import edu.co.icesi.proyectofinal.repository.RoutineExerciseRepository;
import edu.co.icesi.proyectofinal.services.impl.RoutineExerciseServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RoutineExerciseServiceTest {

    @Mock
    private RoutineExerciseRepository routineExerciseRepository;

    @InjectMocks
    private RoutineExerciseServiceImpl routineExerciseService;

    private RoutineExercise routineExercise;
    private RoutineExerciseId id;

    @BeforeEach
    void setUp() {
        Routine routine = new Routine();
        routine.setIdRoutine(1);

        Exercise exercise = new Exercise();
        exercise.setIdExercise(10);

        id = new RoutineExerciseId();
        id.setRoutineId(1);
        id.setExerciseId(10);

        routineExercise = new RoutineExercise();
        routineExercise.setId(id);
        routineExercise.setRoutine(routine);
        routineExercise.setExercise(exercise);
        routineExercise.setSets(3);
        routineExercise.setTargetReps(12);
        routineExercise.setExerciseOrder(1);
    }

    @Test
    void save_shouldReturnSaved() {
        when(routineExerciseRepository.save(routineExercise)).thenReturn(routineExercise);

        RoutineExercise result = routineExerciseService.save(routineExercise);

        assertNotNull(result);
        assertEquals(id, result.getId());
        verify(routineExerciseRepository).save(routineExercise);
    }

    @Test
    void deleteById_shouldCallRepository() {
        when(routineExerciseRepository.existsById(id)).thenReturn(true);
        doNothing().when(routineExerciseRepository).deleteById(id);
        routineExerciseService.deleteById(id);
        verify(routineExerciseRepository).deleteById(id);
    }

    @Test
    void getById_whenExists_shouldReturn() {
        when(routineExerciseRepository.findById(id)).thenReturn(Optional.of(routineExercise));

        RoutineExercise result = routineExerciseService.getById(id);

        assertNotNull(result);
        assertEquals(3, result.getSets());
        assertEquals(12, result.getTargetReps());
    }

    @Test
    void getById_whenNotExists_shouldThrowRuntimeException() {
        when(routineExerciseRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> routineExerciseService.getById(id));

        assertTrue(ex.getMessage().contains("not found"));
    }

    @Test
    void getById_exceptionMessage_shouldContainIds() {
        when(routineExerciseRepository.findById(id)).thenReturn(Optional.empty());

        RuntimeException ex = assertThrows(RuntimeException.class,
                () -> routineExerciseService.getById(id));

        assertTrue(ex.getMessage().contains("1"));
        assertTrue(ex.getMessage().contains("10"));
    }

    @Test
    void findAll_shouldReturnList() {
        when(routineExerciseRepository.findAll()).thenReturn(List.of(routineExercise));

        List<RoutineExercise> result = routineExerciseService.findAll();

        assertEquals(1, result.size());
        verify(routineExerciseRepository).findAll();
    }

    @Test
    void findAll_whenEmpty_shouldReturnEmptyList() {
        when(routineExerciseRepository.findAll()).thenReturn(List.of());

        assertTrue(routineExerciseService.findAll().isEmpty());
    }

    @Test
    void getByRoutineId_shouldReturnFiltered() {
        when(routineExerciseRepository.findByRoutineIdRoutine(1)).thenReturn(List.of(routineExercise));

        List<RoutineExercise> result = routineExerciseService.getByRoutineId(1);

        assertEquals(1, result.size());
        assertEquals(1, result.get(0).getId().getRoutineId());
    }

    @Test
    void getByRoutineId_whenNoMatch_shouldReturnEmptyList() {
        when(routineExerciseRepository.findByRoutineIdRoutine(99)).thenReturn(List.of());

        assertTrue(routineExerciseService.getByRoutineId(99).isEmpty());
    }

    @Test
    void getByExerciseId_shouldReturnFiltered() {
        when(routineExerciseRepository.findByExerciseIdExercise(10)).thenReturn(List.of(routineExercise));

        List<RoutineExercise> result = routineExerciseService.getByExerciseId(10);

        assertEquals(1, result.size());
        assertEquals(10, result.get(0).getId().getExerciseId());
    }

    @Test
    void getByExerciseId_whenNoMatch_shouldReturnEmptyList() {
        when(routineExerciseRepository.findByExerciseIdExercise(99)).thenReturn(List.of());

        assertTrue(routineExerciseService.getByExerciseId(99).isEmpty());
    }

    @Test
    void update_whenExists_shouldUpdateFields() {
        RoutineExercise updated = new RoutineExercise();
        updated.setSets(5);
        updated.setTargetReps(20);
        updated.setExerciseOrder(2);

        when(routineExerciseRepository.findById(id)).thenReturn(Optional.of(routineExercise));
        when(routineExerciseRepository.save(any(RoutineExercise.class)))
                .thenAnswer(x -> x.getArgument(0));

        RoutineExercise result = routineExerciseService.update(id, updated);

        assertEquals(5, result.getSets());
        assertEquals(20, result.getTargetReps());
        assertEquals(2, result.getExerciseOrder());
    }

    @Test
    void update_whenNotExists_shouldThrowRuntimeException() {
        when(routineExerciseRepository.findById(id)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> routineExerciseService.update(id, new RoutineExercise()));
    }

    @Test
    void countByRoutineId_shouldReturnCount() {
        when(routineExerciseRepository.countByRoutineIdRoutine(1)).thenReturn(4L);

        assertEquals(4L, routineExerciseService.countByRoutineId(1));
    }

    @Test
    void countByRoutineId_whenNone_shouldReturnZero() {
        when(routineExerciseRepository.countByRoutineIdRoutine(99)).thenReturn(0L);

        assertEquals(0L, routineExerciseService.countByRoutineId(99));
    }
}