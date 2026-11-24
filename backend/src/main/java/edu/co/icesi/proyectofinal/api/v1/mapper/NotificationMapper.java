package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.NotificationResponse;
import edu.co.icesi.proyectofinal.entity.Notification;
import edu.co.icesi.proyectofinal.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface NotificationMapper {

    @Mapping(source = "userTarget.idUser", target = "userTargetId")
    @Mapping(source = "userSource.idUser", target = "userSourceId")
    @Mapping(source = "userSource", target = "userSourceName", qualifiedByName = "sourceName")
    @Mapping(source = "read", target = "read")
    NotificationResponse toResponse(Notification notification);

    @Named("sourceName")
    default String sourceName(User user) {
        return user == null ? "Mi Bienestar" : user.getFirstName() + " " + user.getLastName();
    }
}
