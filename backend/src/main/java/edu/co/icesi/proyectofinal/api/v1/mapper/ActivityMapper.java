package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.ActivityRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.ActivityResponse;
import edu.co.icesi.proyectofinal.entity.Activity;
import edu.co.icesi.proyectofinal.entity.Space;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface ActivityMapper {
    @Mapping(source = "space.idSpace", target = "spaceId")
    @Mapping(source = "space.name", target = "spaceName")
    @Mapping(source = "space.location", target = "spaceLocation")
    ActivityResponse toResponse(Activity activity);

    @Mapping(source = "spaceId", target = "space", qualifiedByName = "idToSpace")
    @Mapping(target = "idActivity", ignore = true)
    Activity toEntity(ActivityRequest request);

    @Named("idToSpace")
    default Space idToSpace(Integer id) {
        if (id == null) {
            return null;
        }
        Space space = new Space();
        space.setIdSpace(id);
        return space;
    }
}
