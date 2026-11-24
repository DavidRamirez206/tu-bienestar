package edu.co.icesi.proyectofinal.api.v1.dto;

import lombok.Data;

@Data
public class PolicyRequest {
    private String name;
    private String description;
    private String resource;
    private String action;
}
