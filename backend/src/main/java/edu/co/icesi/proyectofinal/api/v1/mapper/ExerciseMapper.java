package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.ExerciseRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.ExerciseResponse;
import edu.co.icesi.proyectofinal.entity.Exercise;
import edu.co.icesi.proyectofinal.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ExerciseMapper {


    @Mapping(source = "userExercise.idUser", target = "userId")
    ExerciseResponse toResponse(Exercise exercise);


    @Mapping(source = "userId", target = "userExercise", qualifiedByName = "idToUser")
    @Mapping(source = "predefined", target = "predefined")
    @Mapping(target = "exercisesRoutines", ignore = true)
    @Mapping(target = "idExercise", ignore = true)
    Exercise toEntity(ExerciseRequest exerciseRequest);

    @Named("idToUser")
    default User idToUser(Integer id) {
        if (id == null) return null;
        User user = new User();
        user.setIdUser(id);
        return user;
    }
}
