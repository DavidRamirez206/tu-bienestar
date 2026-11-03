package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.ActivityRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.ActivityResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.ActivityMapper;
import edu.co.icesi.proyectofinal.services.ActivityService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
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

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("/api/v1/activities")
@Tag(name = "Activities", description = "Eventos y actividades de los espacios deportivos")
public class ActivityRestController {

    private final ActivityService activityService;
    private final ActivityMapper activityMapper;

    public ActivityRestController(ActivityService activityService, ActivityMapper activityMapper) {
        this.activityService = activityService;
        this.activityMapper = activityMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LIST_ACTIVITY')")
    public ResponseEntity<List<ActivityResponse>> getAll(
            @RequestParam(defaultValue = "false") boolean upcoming,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate from,
            @RequestParam(required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate to) {
        return ResponseEntity.ok(activityService.search(upcoming, from, to).stream()
                .map(activityMapper::toResponse)
                .toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LIST_ACTIVITY')")
    public ResponseEntity<ActivityResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(activityMapper.toResponse(activityService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_ACTIVITY')")
    public ResponseEntity<ActivityResponse> create(@Valid @RequestBody ActivityRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(activityMapper.toResponse(activityService.save(activityMapper.toEntity(request))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_ACTIVITY')")
    public ResponseEntity<ActivityResponse> update(@PathVariable Integer id, @Valid @RequestBody ActivityRequest request) {
        return ResponseEntity.ok(activityMapper.toResponse(activityService.update(id, activityMapper.toEntity(request))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_ACTIVITY')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        activityService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
