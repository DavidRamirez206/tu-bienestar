package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.Progress;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.repository.ProgressRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.services.ProgressService;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class ProgressServiceIntegrationTest {

    @Autowired
    private ProgressService progressService;

    @Autowired
    private ProgressRepository progressRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setFirstName("Ana");
        user.setLastName("Torres");
        user.setInstitutionalEmail("ana.torres@icesi.edu.co");
        user.setPassword("pass123");
        userRepository.save(user);
    }

    private Progress buildProgress(int reps) {
        Progress p = new Progress();
        p.setUserProgress(user);
        p.setDateLogged(LocalDateTime.now());
        p.setReps(reps);
        p.setDurationMin(20);
        p.setEffortLevel(6);
        p.setSetNumber(3);
        p.setWeightKg(40);
        return p;
    }

    @Test
    void createProgress_shouldPersist() {
        Progress saved = progressService.createProgress(buildProgress(10));

        assertNotNull(saved.getIdProgress());
    }

    @Test
    void getProgress_byId_whenExists_shouldReturn() {
        Progress saved = progressRepository.save(buildProgress(12));

        Progress result = progressService.getProgress(saved.getIdProgress());

        assertEquals(12, result.getReps());
    }

    @Test
    void getProgress_byId_whenNotExists_shouldThrowRuntimeException() {
        assertThrows(RuntimeException.class, () -> progressService.getProgress(999));
    }

    @Test
    void updateProgress_shouldReflectChanges() {
        Progress saved = progressRepository.save(buildProgress(8));
        saved.setReps(20);
        saved.setWeightKg(60);

        Progress result = progressService.updateProgress(saved);

        assertEquals(20, result.getReps());
        assertEquals(60, result.getWeightKg());
    }

    @Test
    void deleteProgress_shouldRemove() {
        Progress saved = progressRepository.save(buildProgress(5));

        progressService.deleteProgress(saved.getIdProgress());

        assertTrue(progressRepository.findAll().isEmpty());
    }

    @Test
    void getProgress_list_shouldReturnAll() {
        progressRepository.save(buildProgress(10));
        progressRepository.save(buildProgress(15));

        assertEquals(2, progressService.getProgress().size());
    }

    @Test
    void getByUserId_shouldReturnFiltered() {
        progressRepository.save(buildProgress(10));
        progressRepository.save(buildProgress(20));

        List<Progress> result = progressService.getByUserId(user.getIdUser());

        assertEquals(2, result.size());
    }
}