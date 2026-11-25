package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.RolePolicyRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.RolePolicyResponse;
import edu.co.icesi.proyectofinal.entity.RolePolicy;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface RolePolicyMapper {

    @Mapping(target = "id.roleId", source = "roleId")
    @Mapping(target = "id.policyId", source = "policyId")
    @Mapping(target = "role.id", source = "roleId")
    @Mapping(target = "policy.id", source = "policyId")
    RolePolicy toEntity(RolePolicyRequest request);

    @Mapping(source = "id.roleId", target = "roleId")
    @Mapping(source = "id.policyId", target = "policyId")
    RolePolicyResponse toResponse(RolePolicy entity);
}
