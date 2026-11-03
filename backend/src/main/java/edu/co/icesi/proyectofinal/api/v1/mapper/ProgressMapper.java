package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.ProgressRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.ProgressResponse;
import edu.co.icesi.proyectofinal.entity.Progress;
import org.mapstruct.AfterMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

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

    @AfterMapping
    default void clearEmptyReferences(@MappingTarget Progress progress) {
        if (progress.getRoutine() != null && progress.getRoutine().getIdRoutine() == null) {
            progress.setRoutine(null);
        }
        if (progress.getExercise() != null && progress.getExercise().getIdExercise() == null) {
            progress.setExercise(null);
        }
        if (progress.getUserProgress() != null && progress.getUserProgress().getIdUser() == null) {
            progress.setUserProgress(null);
        }
    }
}
