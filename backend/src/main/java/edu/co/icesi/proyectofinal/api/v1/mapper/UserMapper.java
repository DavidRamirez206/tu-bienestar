package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.UserRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.UserResponse;
import edu.co.icesi.proyectofinal.api.v1.dto.RegisterRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.RegisterResponse;
import edu.co.icesi.proyectofinal.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserMapper {

    UserResponse toResponse(User user);

    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "progresses", ignore = true)
    @Mapping(target = "routines", ignore = true)
    @Mapping(target = "userExercises", ignore = true)
    @Mapping(target = "idUser", ignore = true)
    //@Mapping(target = "password", ignore = true)
    User toEntity(UserRequest userRequest);

    @Mapping(target = "idUser", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "progresses", ignore = true)
    @Mapping(target = "routines", ignore = true)
    @Mapping(target = "userExercises", ignore = true)
    User registerRequestToUser(RegisterRequest registerRequest);

    @Mapping(target = "message", constant = "User registered successfully")
    RegisterResponse toRegisterResponse(User user);
}
