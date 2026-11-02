package edu.co.icesi.proyectofinal.repository;

import edu.co.icesi.proyectofinal.entity.Recommendation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RecommendationRepository extends JpaRepository<Recommendation, Integer> {

    List<Recommendation> findByUserIdUser(Integer userId);

    List<Recommendation> findByTrainerIdUser(Integer trainerId);

    Optional<Recommendation> findTopByUserIdUserOrderByDateCreatedDesc(Integer userId);

    long countByTrainerIdUser(Integer trainerId);

}
