package edu.co.icesi.proyectofinal.api.v1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleRequest {
    @NotNull(message = "El evento es obligatorio")
    private Integer activityId;
    /** MONDAY ... SUNDAY. */
    @NotBlank(message = "El día es obligatorio")
    private String dayOfWeek;
    @NotNull(message = "La hora de inicio es obligatoria")
    private LocalTime startTime;
    @NotNull(message = "La hora de fin es obligatoria")
    private LocalTime endTime;
}
