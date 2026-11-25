package edu.co.icesi.proyectofinal.unit;

import edu.co.icesi.proyectofinal.entity.Recommendation;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.repository.RecommendationRepository;
import edu.co.icesi.proyectofinal.services.impl.RecommendationServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class RecommendationServiceTest {

    @Mock
    private RecommendationRepository recommendationRepository;

    @Mock
    private edu.co.icesi.proyectofinal.services.NotificationService notificationService;

    @InjectMocks
    private RecommendationServiceImpl recommendationService;

    private Recommendation recommendation;

    @BeforeEach
    void setUp() {
        User user = new User();
        user.setIdUser(1);
        user.setInstitutionalEmail("user@icesi.edu.co");

        User trainer = new User();
        trainer.setIdUser(2);
        trainer.setInstitutionalEmail("trainer@icesi.edu.co");

        recommendation = new Recommendation();
        recommendation.setIdRecommendation(1);
        recommendation.setUser(user);
        recommendation.setTrainer(trainer);
        recommendation.setDescription("Aumentar cardio");
        recommendation.setDateCreated(LocalDate.now());
    }

    @Test
    void getRecommendations_shouldReturnList() {
        when(recommendationRepository.findAll()).thenReturn(List.of(recommendation));

        assertEquals(1, recommendationService.getRecommendations().size());
    }

    @Test
    void getRecommendation_whenExists_shouldReturn() {
        when(recommendationRepository.findById(1)).thenReturn(Optional.of(recommendation));

        Recommendation result = recommendationService.getRecommendation(1);

        assertNotNull(result);
        assertEquals("Aumentar cardio", result.getDescription());
    }

    @Test
    void getRecommendation_whenNotExists_shouldThrowRuntimeException() {
        when(recommendationRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class, () -> recommendationService.getRecommendation(99));
    }

    @Test
    void createRecommendation_shouldReturnSaved() {
        when(recommendationRepository.save(recommendation)).thenReturn(recommendation);

        Recommendation result = recommendationService.createRecommendation(recommendation);

        assertNotNull(result);
        assertEquals("Aumentar cardio", result.getDescription());
    }

    @Test
    void updateRecommendation_whenExists_shouldUpdateAndReturn() {
        Recommendation updated = new Recommendation();
        updated.setIdRecommendation(1);
        updated.setDescription("Reducir peso");
        updated.setDateCreated(LocalDate.now());
        updated.setUser(recommendation.getUser());
        updated.setTrainer(recommendation.getTrainer());

        when(recommendationRepository.findById(1)).thenReturn(Optional.of(recommendation));
        when(recommendationRepository.save(any(Recommendation.class))).thenAnswer(x -> x.getArgument(0));

        Recommendation result = recommendationService.updateRecommendation(updated);

        assertEquals("Reducir peso", result.getDescription());
    }

    @Test
    void updateRecommendation_whenNotExists_shouldThrowRuntimeException() {
        recommendation.setIdRecommendation(99);
        when(recommendationRepository.findById(99)).thenReturn(Optional.empty());

        assertThrows(RuntimeException.class,
                () -> recommendationService.updateRecommendation(recommendation));
    }

    @Test
    void deleteRecommendation_shouldCallRepository() {
        when(recommendationRepository.existsById(1)).thenReturn(true);
        doNothing().when(recommendationRepository).deleteById(1);
        recommendationService.deleteRecommendation(1);
        verify(recommendationRepository).deleteById(1);
    }

    @Test
    void getByUserId_shouldReturnFilteredList() {
        when(recommendationRepository.findByUserIdUser(1)).thenReturn(List.of(recommendation));

        assertEquals(1, recommendationService.getByUserId(1).size());
    }

    @Test
    void getByTrainerId_shouldReturnFilteredList() {
        when(recommendationRepository.findByTrainerIdUser(2)).thenReturn(List.of(recommendation));

        assertEquals(1, recommendationService.getByTrainerId(2).size());
    }
}