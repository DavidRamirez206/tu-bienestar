package edu.co.icesi.proyectofinal.api.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RegisterRequest {
    @NotBlank
    private String firstName;
    @NotBlank
    private String lastName;
    @NotBlank
    @Pattern(regexp = "^[A-Za-z0-9._%+-]+@(icesi\\.edu\\.co|u\\.icesi\\.edu\\.co)$",
            message = "Email must be from icesi.edu.co or u.icesi.edu.co")
    private String institutionalEmail;
    @NotBlank
    private String password;
    private Integer age;
}
