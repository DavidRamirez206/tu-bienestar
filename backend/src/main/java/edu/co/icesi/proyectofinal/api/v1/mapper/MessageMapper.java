package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.MessageResponse;
import edu.co.icesi.proyectofinal.entity.Message;
import edu.co.icesi.proyectofinal.entity.User;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

@Mapper(componentModel = "spring")
public interface MessageMapper {

    @Mapping(source = "sender.idUser", target = "senderId")
    @Mapping(source = "sender", target = "senderName", qualifiedByName = "fullName")
    @Mapping(source = "receiver.idUser", target = "receiverId")
    @Mapping(source = "receiver", target = "receiverName", qualifiedByName = "fullName")
    MessageResponse toResponse(Message message);

    @Named("fullName")
    default String fullName(User user) {
        return user == null ? null : user.getFirstName() + " " + user.getLastName();
    }
}
