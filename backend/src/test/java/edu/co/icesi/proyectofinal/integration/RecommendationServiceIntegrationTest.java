package edu.co.icesi.proyectofinal.integration;

import edu.co.icesi.proyectofinal.entity.Recommendation;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.repository.RecommendationRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.services.RecommendationService;
import org.springframework.transaction.annotation.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.annotation.Rollback;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@Transactional
@Rollback
class RecommendationServiceIntegrationTest {

    @Autowired
    private RecommendationService recommendationService;

    @Autowired
    private RecommendationRepository recommendationRepository;

    @Autowired
    private UserRepository userRepository;

    private User user;
    private User trainer;

    @BeforeEach
    void setUp() {

        user = new User();
        user.setFirstName("Pedro");
        user.setLastName("Mora");
        user.setInstitutionalEmail("pedro.mora@icesi.edu.co");
        user.setPassword("pass");
        userRepository.save(user);

        trainer = new User();
        trainer.setFirstName("Laura");
        trainer.setLastName("Gil");
        trainer.setInstitutionalEmail("laura.gil@icesi.edu.co");
        trainer.setPassword("pass");
        userRepository.save(trainer);
    }

    private Recommendation buildRecommendation(String desc) {
        Recommendation r = new Recommendation();
        r.setUser(user);
        r.setTrainer(trainer);
        r.setDescription(desc);
        r.setDateCreated(LocalDate.now());
        return r;
    }

    @Test
    void createRecommendation_shouldPersist() {
        Recommendation saved = recommendationService.createRecommendation(
                buildRecommendation("Aumentar cardio"));

        assertNotNull(saved.getIdRecommendation());
    }

    @Test
    void getRecommendation_whenExists_shouldReturn() {
        Recommendation saved = recommendationRepository.save(
                buildRecommendation("Reducir azúcar"));

        Recommendation result = recommendationService.getRecommendation(saved.getIdRecommendation());

        assertEquals("Reducir azúcar", result.getDescription());
    }

    @Test
    void getRecommendation_whenNotExists_shouldThrowRuntimeException() {
        assertThrows(RuntimeException.class, () -> recommendationService.getRecommendation(999));
    }

    @Test
    void updateRecommendation_shouldReflectChanges() {
        Recommendation saved = recommendationRepository.save(
                buildRecommendation("Viejo"));
        saved.setDescription("Nuevo");

        Recommendation result = recommendationService.updateRecommendation(saved);

        assertEquals("Nuevo", result.getDescription());
    }

    @Test
    void deleteRecommendation_shouldRemove() {
        Recommendation saved = recommendationRepository.save(
                buildRecommendation("A borrar"));

        recommendationService.deleteRecommendation(saved.getIdRecommendation());

        assertTrue(recommendationRepository.findAll().isEmpty());
    }

    @Test
    void getRecommendations_shouldReturnAll() {
        recommendationRepository.save(buildRecommendation("R1"));
        recommendationRepository.save(buildRecommendation("R2"));

        assertEquals(2, recommendationService.getRecommendations().size());
    }

    @Test
    void getByUserId_shouldReturnFiltered() {
        recommendationRepository.save(buildRecommendation("Para Pedro"));

        List<Recommendation> result = recommendationService.getByUserId(user.getIdUser());

        assertEquals(1, result.size());
    }

    @Test
    void getByTrainerId_shouldReturnFiltered() {
        recommendationRepository.save(buildRecommendation("De Laura"));

        List<Recommendation> result = recommendationService.getByTrainerId(trainer.getIdUser());

        assertEquals(1, result.size());
    }
}