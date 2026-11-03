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
public class ActivityResponse {
    private Integer idActivity;
    private String name;
    private String description;
    private LocalDate startDate;
    private LocalDate endDate;
    private Integer spaceId;
    private String spaceName;
    private String spaceLocation;
}
