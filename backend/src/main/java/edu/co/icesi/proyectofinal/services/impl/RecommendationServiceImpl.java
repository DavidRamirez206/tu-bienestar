package edu.co.icesi.proyectofinal.services.impl;

import edu.co.icesi.proyectofinal.entity.Recommendation;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.repository.RecommendationRepository;
import edu.co.icesi.proyectofinal.repository.UserRepository;
import edu.co.icesi.proyectofinal.services.NotificationService;
import edu.co.icesi.proyectofinal.services.RecommendationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
public class RecommendationServiceImpl implements RecommendationService {

    private final RecommendationRepository recommendationRepository;
    private final UserRepository userRepository;
    private final NotificationService notificationService;

    public RecommendationServiceImpl(RecommendationRepository recommendationRepository, 
                                   UserRepository userRepository,
                                   NotificationService notificationService) {
        this.recommendationRepository = recommendationRepository;
        this.userRepository = userRepository;
        this.notificationService = notificationService;
    }

    @Override
    public List<Recommendation> getRecommendations() {
        return recommendationRepository.findAll();
    }

    @Override
    public Recommendation getRecommendation(Integer id) {
        return recommendationRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Recommendation with id " + id + " not found"));
    }

    @Override
    @Transactional
    public Recommendation createRecommendation(Recommendation recommendation) {
        if(recommendation.getDateCreated() == null){
            recommendation.setDateCreated(LocalDate.now());
        }

        // Fetch full entities to avoid 500 errors when creating notifications
        User trainer = userRepository.findById(recommendation.getTrainer().getIdUser())
                .orElseThrow(() -> new RuntimeException("Trainer not found"));
        User trainee = userRepository.findById(recommendation.getUser().getIdUser())
                .orElseThrow(() -> new RuntimeException("Trainee not found"));

        recommendation.setTrainer(trainer);
        recommendation.setUser(trainee);

        Recommendation saved = recommendationRepository.save(recommendation);
        
        // HU15: el alumno recibe el aviso en vivo y queda guardado si no estaba conectado.
        notificationService.notifyUser(trainee, trainer, NotificationService.TYPE_NEW_RECOMMENDATION,
                "Nueva recomendación de " + trainer.getFirstName() + " " + trainer.getLastName(),
                saved.getIdRecommendation(), NotificationService.REF_RECOMMENDATION);
        
        return saved;
    }

    @Override
    public Recommendation updateRecommendation(Recommendation recommendation) {
        Recommendation existing = recommendationRepository.findById(recommendation.getIdRecommendation())
                .orElseThrow(() -> new RuntimeException("Recommendation with id " + recommendation.getIdRecommendation() + " not found"));
        existing.setDescription(recommendation.getDescription());
        existing.setDateCreated(recommendation.getDateCreated());
        existing.setUser(recommendation.getUser());
        existing.setTrainer(recommendation.getTrainer());

        if(recommendation.getDateCreated() == null){
            existing.setDateCreated(LocalDate.now());
        }else{
            existing.setDateCreated(recommendation.getDateCreated());
        }

        return recommendationRepository.save(existing);
    }

    @Override
    public void deleteRecommendation(Integer id) {

        if(!recommendationRepository.existsById(id)){
            throw new RuntimeException("Recommendation with id " + id +
                    " not found");
        }
        recommendationRepository.deleteById(id);
    }

    @Override
    public List<Recommendation> getByUserId(Integer userId) {
        return recommendationRepository.findByUserIdUser(userId);
    }

    @Override
    public List<Recommendation> getByTrainerId(Integer trainerId) {
        return recommendationRepository.findByTrainerIdUser(trainerId);
    }
}