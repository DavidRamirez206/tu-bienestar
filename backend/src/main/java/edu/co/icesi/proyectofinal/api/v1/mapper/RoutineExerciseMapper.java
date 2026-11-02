package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.RoutineExerciseRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.RoutineExerciseResponse;
import edu.co.icesi.proyectofinal.entity.RoutineExercise;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RoutineExerciseMapper {

    @Mapping(target = "id.routineId", source = "routineId")
    @Mapping(target = "id.exerciseId", source = "exerciseId")
    @Mapping(target = "routine.idRoutine", source = "routineId")
    @Mapping(target = "exercise.idExercise", source = "exerciseId")
    RoutineExercise toEntity(RoutineExerciseRequest request);

    @Mapping(source = "id.routineId", target = "routineId")
    @Mapping(source = "id.exerciseId", target = "exerciseId")
    @Mapping(source = "exercise.name", target = "exerciseName")
    @Mapping(source = "exercise.type", target = "exerciseType")
    @Mapping(source = "exercise.difficulty", target = "exerciseDifficulty")
    RoutineExerciseResponse toResponse(RoutineExercise entity);
}
