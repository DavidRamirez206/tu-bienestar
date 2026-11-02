package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.RoutineRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.RoutineResponse;
import edu.co.icesi.proyectofinal.entity.Routine;
import edu.co.icesi.proyectofinal.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface RoutineMapper {

    @Mapping(source = "userRoutine.idUser", target = "userId")
    @Mapping(source = "predesigned", target = "predesigned")
    RoutineResponse toResponse(Routine routine);


    @Mapping(source = "userId", target = "userRoutine", qualifiedByName = "idToUser")
    @Mapping(source = "predesigned", target = "predesigned")
    @Mapping(target = "routineExercises", ignore = true)
    @Mapping(target = "idRoutine", ignore = true)
    Routine toEntity(RoutineRequest routineRequest);


    @Named("idToUser")
    default User idToUser(Integer id) {
        if (id == null) return null;
        User user = new User();
        user.setIdUser(id);
        return user;
    }
}
