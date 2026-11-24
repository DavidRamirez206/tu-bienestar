package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.RecommendationRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.RecommendationResponse;
import edu.co.icesi.proyectofinal.entity.Recommendation;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RecommendationMapper {
    @Mapping(target = "trainer.idUser", source = "trainerId")
    @Mapping(target = "user.idUser", source = "userId")
    Recommendation toEntity(RecommendationRequest request);

    @Mapping(target = "trainerId", source = "trainer.idUser")
    @Mapping(target = "trainerFirstName", source = "trainer.firstName")
    @Mapping(target = "trainerLastName", source = "trainer.lastName")
    @Mapping(target = "userId", source = "user.idUser")
    @Mapping(target = "userFirstName", source = "user.firstName")
    @Mapping(target = "userLastName", source = "user.lastName")
    RecommendationResponse toResponse(Recommendation recommendation);
}
