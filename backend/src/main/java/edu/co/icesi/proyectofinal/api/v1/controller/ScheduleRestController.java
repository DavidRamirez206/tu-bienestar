package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.ScheduleRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.ScheduleResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.ScheduleMapper;
import edu.co.icesi.proyectofinal.entity.Schedule;
import edu.co.icesi.proyectofinal.services.ScheduleService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/schedules")
@Tag(name = "Schedules", description = "Horarios del gimnasio y de las clases")
public class ScheduleRestController {

    private final ScheduleService scheduleService;
    private final ScheduleMapper scheduleMapper;

    public ScheduleRestController(ScheduleService scheduleService, ScheduleMapper scheduleMapper) {
        this.scheduleService = scheduleService;
        this.scheduleMapper = scheduleMapper;
    }

    /** Horarios de la semana, opcionalmente de un dia y de un tipo de evento. */
    @GetMapping
    @PreAuthorize("hasAuthority('LIST_SCHEDULE')")
    public ResponseEntity<List<ScheduleResponse>> getAll(@RequestParam(required = false) String day,
                                                         @RequestParam(required = false) String type) {
        return ResponseEntity.ok(toResponses(scheduleService.search(day, type)));
    }

    @GetMapping("/activity/{activityId}")
    @PreAuthorize("hasAuthority('LIST_SCHEDULE')")
    public ResponseEntity<List<ScheduleResponse>> getByActivity(@PathVariable Integer activityId) {
        return ResponseEntity.ok(toResponses(scheduleService.findByActivityId(activityId)));
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LIST_SCHEDULE')")
    public ResponseEntity<ScheduleResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(scheduleMapper.toResponse(scheduleService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_SCHEDULE')")
    public ResponseEntity<ScheduleResponse> create(@Valid @RequestBody ScheduleRequest request) {
        Schedule schedule = scheduleService.create(request.getActivityId(), request.getDayOfWeek(),
                request.getStartTime(), request.getEndTime());
        return ResponseEntity.status(HttpStatus.CREATED).body(scheduleMapper.toResponse(schedule));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_SCHEDULE')")
    public ResponseEntity<ScheduleResponse> update(@PathVariable Integer id, @Valid @RequestBody ScheduleRequest request) {
        Schedule schedule = scheduleService.update(id, request.getActivityId(), request.getDayOfWeek(),
                request.getStartTime(), request.getEndTime());
        return ResponseEntity.ok(scheduleMapper.toResponse(schedule));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_SCHEDULE')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        scheduleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    private List<ScheduleResponse> toResponses(List<Schedule> schedules) {
        return schedules.stream().map(scheduleMapper::toResponse).toList();
    }
}
