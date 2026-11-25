package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.UserRoleRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.UserRoleResponse;
import edu.co.icesi.proyectofinal.entity.UserRole;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface UserRoleMapper {

    @Mapping(target = "id.userId", source = "userId")
    @Mapping(target = "id.roleId", source = "roleId")
    @Mapping(target = "user.idUser", source = "userId")
    @Mapping(target = "role.id", source = "roleId")
    UserRole toEntity(UserRoleRequest request);

    @Mapping(source = "id.userId", target = "userId")
    @Mapping(source = "id.roleId", target = "roleId")
    UserRoleResponse toResponse(UserRole entity);
}
