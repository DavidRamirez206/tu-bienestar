package edu.co.icesi.proyectofinal.api.v1.mapper;

import edu.co.icesi.proyectofinal.api.v1.dto.ScheduleResponse;
import edu.co.icesi.proyectofinal.entity.Schedule;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface ScheduleMapper {
    @Mapping(source = "activity.idActivity", target = "activityId")
    @Mapping(source = "activity.name", target = "activityName")
    @Mapping(source = "activity.type", target = "activityType")
    @Mapping(source = "activity.space.name", target = "spaceName")
    ScheduleResponse toResponse(Schedule schedule);
}
