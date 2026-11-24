package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.AssignmentResponse;
import edu.co.icesi.proyectofinal.entity.Assignment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AssignmentMapper {
    @Mapping(source = "id.userId", target = "userId")
    @Mapping(source = "userAssignment.firstName", target = "userFirstName")
    @Mapping(source = "userAssignment.lastName", target = "userLastName")
    @Mapping(source = "userAssignment.institutionalEmail", target = "userEmail")
    @Mapping(source = "id.trainerId", target = "trainerId")
    @Mapping(source = "trainerAssignment.firstName", target = "trainerFirstName")
    @Mapping(source = "trainerAssignment.lastName", target = "trainerLastName")
    AssignmentResponse toResponse(Assignment assignment);
}
