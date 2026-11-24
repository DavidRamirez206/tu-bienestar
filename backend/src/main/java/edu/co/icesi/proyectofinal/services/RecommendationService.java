package edu.co.icesi.proyectofinal.services;

import edu.co.icesi.proyectofinal.entity.Recommendation;

import java.util.List;

public interface RecommendationService {

    List<Recommendation> getRecommendations();

    Recommendation getRecommendation(Integer id);

    Recommendation createRecommendation(Recommendation recommendation);

    Recommendation updateRecommendation(Recommendation recommendation);

    void deleteRecommendation(Integer id);

    List<Recommendation> getByUserId(Integer userId);

    List<Recommendation> getByTrainerId(Integer trainerId);
}
