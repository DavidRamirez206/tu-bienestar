package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.SpaceRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.SpaceResponse;
import edu.co.icesi.proyectofinal.entity.Space;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface SpaceMapper {
    @Mapping(target = "idSpace", ignore = true)
    Space toEntity(SpaceRequest request);

    SpaceResponse toResponse(Space space);
}
