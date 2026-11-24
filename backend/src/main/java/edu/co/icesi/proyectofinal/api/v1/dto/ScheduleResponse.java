package edu.co.icesi.proyectofinal.api.v1.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class ScheduleResponse {
    private Integer idSchedule;
    private Integer activityId;
    private String activityName;
    private String activityType;
    private String spaceName;
    private String dayOfWeek;
    private LocalTime startTime;
    private LocalTime endTime;
}
