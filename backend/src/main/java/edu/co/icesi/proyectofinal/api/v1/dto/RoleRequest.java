package edu.co.icesi.proyectofinal.api.v1.dto;

import lombok.Data;

import java.util.List;

@Data
public class RoleRequest {
    private String name;
    private List<Long> policyIds;
}
