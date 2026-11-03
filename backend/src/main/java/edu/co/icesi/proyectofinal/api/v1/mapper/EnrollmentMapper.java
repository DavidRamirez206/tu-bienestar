package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.EnrollmentResponse;
import edu.co.icesi.proyectofinal.entity.Enrollment;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = ActivityMapper.class)
public interface EnrollmentMapper {
    @Mapping(source = "id.userId", target = "userId")
    @Mapping(source = "id.activityId", target = "activityId")
    EnrollmentResponse toResponse(Enrollment enrollment);
}
