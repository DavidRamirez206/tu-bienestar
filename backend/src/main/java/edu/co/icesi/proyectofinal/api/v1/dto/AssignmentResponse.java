package edu.co.icesi.proyectofinal.api.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AssignmentResponse {
    private Integer userId;
    private String userFirstName;
    private String userLastName;
    private String userEmail;
    private Integer trainerId;
    private String trainerFirstName;
    private String trainerLastName;
    private LocalDate assignmentDate;
}
