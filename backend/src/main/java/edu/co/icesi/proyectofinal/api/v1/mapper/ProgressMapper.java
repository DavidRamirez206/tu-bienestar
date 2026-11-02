package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.ProgressRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.ProgressResponse;
import edu.co.icesi.proyectofinal.entity.Progress;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ProgressMapper {
    @Mapping(target = "userProgress.idUser", source = "userId")
    @Mapping(target = "exercise.idExercise", source = "exerciseId")
    @Mapping(target = "routine.idRoutine", source = "routineId")
    Progress toEntity(ProgressRequest request);

    @Mapping(target = "userId", source = "userProgress.idUser")
    @Mapping(target = "exerciseId", source = "exercise.idExercise")
    @Mapping(target = "routineId", source = "routine.idRoutine")
    ProgressResponse toResponse(Progress progress);
}
