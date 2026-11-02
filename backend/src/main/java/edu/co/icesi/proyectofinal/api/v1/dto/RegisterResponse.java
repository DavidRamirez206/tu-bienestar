package edu.co.icesi.proyectofinal.api.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterResponse {
    private Integer idUser;
    private String firstName;
    private String lastName;
    private String institutionalEmail;
    private String message;
}
