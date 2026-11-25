package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.PolicyRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.PolicyResponse;
import edu.co.icesi.proyectofinal.entity.Policy;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface PolicyMapper {
    Policy toEntity(PolicyRequest request);
    PolicyResponse toResponse(Policy policy);
}
