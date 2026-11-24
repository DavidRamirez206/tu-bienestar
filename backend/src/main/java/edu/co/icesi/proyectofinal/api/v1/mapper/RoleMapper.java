package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.RoleRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.RoleResponse;
import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.entity.RolePolicy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface RoleMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "userRoles", ignore = true)
    @Mapping(target = "rolePolicies", ignore = true)
    Role toEntity(RoleRequest request);

    @Mapping(target = "policyIds", expression = "java(mapPolicyIds(role))")
    RoleResponse toResponse(Role role);

    default List<Long> mapPolicyIds(Role role) {
        if (role == null || role.getRolePolicies() == null) {
            return Collections.emptyList();
        }

        return role.getRolePolicies()
                .stream()
                .map(RolePolicy::getId)
                .map(id -> id.getPolicyId())
                .toList();
    }
}