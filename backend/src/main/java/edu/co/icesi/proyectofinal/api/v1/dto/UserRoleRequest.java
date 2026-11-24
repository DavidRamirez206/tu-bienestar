package edu.co.icesi.proyectofinal.api.v1.dto;

import lombok.Data;

@Data
public class UserRoleRequest {
    private Integer userId;
    private Long roleId;
}
