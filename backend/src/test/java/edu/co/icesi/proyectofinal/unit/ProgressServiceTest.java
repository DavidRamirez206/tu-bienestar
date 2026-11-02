package edu.co.icesi.proyectofinal.unit;

import edu.co.icesi.proyectofinal.entity.Progress;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.repository.ProgressRepository;
import edu.co.icesi.proyectofinal.services.impl.ProgressServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProgressServiceTest {

    @Mock
    private ProgressRepository progressRepository;

    @InjectMocks
    private ProgressServiceImpl progressService;

    private Progress progress;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setIdUser(1);

        progress = new Progress();
        progress.setIdProgress(1);
        progress.setUserProgress(user);
        progress.setDateLogged(LocalDateTime.now());
        progress.setReps(10);
        progress.setDurationMin(30);
        progress.setEffortLevel(7);
        progress.setSetNumber(3);
        progress.setWeightKg(50);
    }

    @Test
    void getProgress_list_shouldReturnAll() {
        when(progressRepository.findAll()).thenReturn(List.of(progress));

        assertEquals(1, progressService.getProgress().size());
    }

    @Test
    void getProgress_byId_whenExists_shouldReturn() {
        when(progressRepository.findById(1)).thenReturn(Optional.of(progress));

        Progress result = progressService.getProgress(1);

        assertNotNull(result);
        assertEquals(10, result.getReps());
    }

    @Test
    void getProgress_byId_whenNotExists_shouldThrowRuntimeException() {
        when(progressRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> progressService.getProgress(99));
    }

    @Test
    void createProgress_shouldReturnSaved() {
        when(progressRepository.save(progress)).thenReturn(progress);

        Progress result = progressService.createProgress(progress);

        assertNotNull(result);
        assertEquals(30, result.getDurationMin());
    }

    @Test
    void updateProgress_whenExists_shouldUpdateAndReturn() {
        Progress updated = new Progress();
        updated.setIdProgress(1);
        updated.setDateLogged(LocalDateTime.now());
        updated.setReps(15);
        updated.setDurationMin(45);
        updated.setEffortLevel(8);
        updated.setSetNumber(4);
        updated.setWeightKg(60);

        when(progressRepository.findById(1)).thenReturn(Optional.of(progress));
        when(progressRepository.save(any(Progress.class))).thenAnswer(x -> x.getArgument(0));

        Progress result = progressService.updateProgress(updated);

        assertEquals(15, result.getReps());
        assertEquals(60, result.getWeightKg());
    }

    @Test
    void updateProgress_whenNotExists_shouldThrowRuntimeException() {
        progress.setIdProgress(99);
        when(progressRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> progressService.updateProgress(progress));
    }

    @Test
    void deleteProgress_shouldCallRepository() {
        when(progressRepository.existsById(1)).thenReturn(true);
        doNothing().when(progressRepository).deleteById(1);
        progressService.deleteProgress(1);
        verify(progressRepository).deleteById(1);
    }

    @Test
    void getByUserId_shouldReturnFilteredList() {
        when(progressRepository.findByUserProgressIdUser(1)).thenReturn(List.of(progress));

        assertEquals(1, progressService.getByUserId(1).size());
    }

    @Test
    void getByRoutineId_shouldReturnFilteredList() {
        when(progressRepository.findByRoutineIdRoutine(5)).thenReturn(List.of(progress));

        assertEquals(1, progressService.getByRoutineId(5).size());
    }

    @Test
    void getByExerciseId_shouldReturnFilteredList() {
        when(progressRepository.findByExerciseIdExercise(3)).thenReturn(List.of(progress));

        assertEquals(1, progressService.getByExerciseId(3).size());
    }
}