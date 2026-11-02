package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.RoutineExerciseRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.RoutineExerciseResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.RoutineExerciseMapper;
import edu.co.icesi.proyectofinal.entity.RoutineExercise;
import edu.co.icesi.proyectofinal.entity.keys.RoutineExerciseId;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.services.RoutineExerciseService;
import edu.co.icesi.proyectofinal.services.RoutineService;
import edu.co.icesi.proyectofinal.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/routine-exercises")
@Tag(name = "Routine Exercises", description = "Endpoints for managing exercises within routines")
public class RoutineExerciseRestController {

    private final RoutineExerciseService routineExerciseService;
    private final RoutineExerciseMapper routineExerciseMapper;
    private final RoutineService routineService;
    private final UserService userService;

    public RoutineExerciseRestController(RoutineExerciseService routineExerciseService,
                                         RoutineExerciseMapper routineExerciseMapper,
                                         RoutineService routineService,
                                         UserService userService) {
        this.routineExerciseService = routineExerciseService;
        this.routineExerciseMapper = routineExerciseMapper;
        this.routineService = routineService;
        this.userService = userService;
    }

    private void assertCanModify(Integer routineId, Principal principal) {
        routineService.assertCanModify(routineId, userService.getUserByEmail(principal.getName()));
    }

    private void assertCanView(Integer routineId, Principal principal) {
        if (!routineService.canView(routineService.findById(routineId), userService.getUserByEmail(principal.getName()))) {
            throw new AccessDeniedException("You cannot view this routine");
        }
    }

    @GetMapping
    @Operation(
            summary = "Get all routine-exercise assignments",
            description = "Retrieves a list of all routine-exercise assignments in the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of routine-exercise assignments retrieved successfully")
            }
    )
    @PreAuthorize("hasAuthority('LIST_ROUTINE_EXERCISE')")
    public ResponseEntity<List<RoutineExerciseResponse>> getAll(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(routineExerciseService.findAll().stream()
                .filter(item -> routineService.canView(item.getRoutine(), user))
                .map(routineExerciseMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{routineId}/{exerciseId}")
    @Operation(
            summary = "Get routine-exercise assignment by IDs",
            description = "Retrieves a single routine-exercise assignment's details by its routine ID and exercise ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Routine-exercise assignment found", 
                            content = @Content(schema = @Schema(implementation = RoutineExerciseResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Routine-exercise assignment not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('LIST_ROUTINE_EXERCISE')")
    public ResponseEntity<RoutineExerciseResponse> getById(@PathVariable Integer routineId, @PathVariable Integer exerciseId, Principal principal) {
        assertCanView(routineId, principal);
        RoutineExerciseId id = new RoutineExerciseId();
        id.setRoutineId(routineId);
        id.setExerciseId(exerciseId);
        return ResponseEntity.ok(routineExerciseMapper.toResponse(routineExerciseService.getById(id)));
    }

    @PostMapping
    @Operation(
            summary = "Create a routine-exercise assignment",
            description = "Creates a new routine-exercise assignment with the provided data",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Routine-exercise assignment created", 
                            content = @Content(schema = @Schema(implementation = RoutineExerciseResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('CREATE_ROUTINE_EXERCISE')")
    public ResponseEntity<RoutineExerciseResponse> create(@RequestBody RoutineExerciseRequest request, Principal principal) {
        assertCanModify(request.getRoutineId(), principal);
        RoutineExercise routineExercise = routineExerciseMapper.toEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(routineExerciseMapper.toResponse(routineExerciseService.save(routineExercise)));
    }

    @PutMapping("/{routineId}/{exerciseId}")
    @Operation(
            summary = "Update a routine-exercise assignment",
            description = "Updates an existing routine-exercise assignment's details by its routine ID and exercise ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Routine-exercise assignment updated successfully", 
                            content = @Content(schema = @Schema(implementation = RoutineExerciseResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Routine-exercise assignment not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('EDIT_ROUTINE_EXERCISE')")
    public ResponseEntity<RoutineExerciseResponse> update(@PathVariable Integer routineId, @PathVariable Integer exerciseId, @RequestBody RoutineExerciseRequest request, Principal principal) {
        assertCanModify(routineId, principal);
        RoutineExerciseId id = new RoutineExerciseId();
        id.setRoutineId(routineId);
        id.setExerciseId(exerciseId);
        
        RoutineExercise routineExercise = routineExerciseMapper.toEntity(request);
        return ResponseEntity.ok(routineExerciseMapper.toResponse(routineExerciseService.update(id, routineExercise)));
    }

    @DeleteMapping("/{routineId}/{exerciseId}")
    @Operation(
            summary = "Delete a routine-exercise assignment",
            description = "Deletes a routine-exercise assignment from the system by its routine ID and exercise ID",
            responses = {
                    @ApiResponse(responseCode = "244", description = "Routine-exercise assignment deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Routine-exercise assignment not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('DELETE_ROUTINE_EXERCISE')")
    public ResponseEntity<Void> delete(@PathVariable Integer routineId, @PathVariable Integer exerciseId, Principal principal) {
        assertCanModify(routineId, principal);
        RoutineExerciseId id = new RoutineExerciseId();
        id.setRoutineId(routineId);
        id.setExerciseId(exerciseId);
        routineExerciseService.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/routine/{routineId}/exercises")
    @Operation(
            summary = "Assign multiple exercises to a routine",
            description = "Creates multiple routine-exercise assignments for a given routine",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Exercises assigned successfully"),
                    @ApiResponse(responseCode = "404", description = "Routine or Exercise not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('CREATE_ROUTINE_EXERCISE')")
    public ResponseEntity<List<RoutineExerciseResponse>> assignExercisesToRoutine(@PathVariable Integer routineId, @RequestBody List<Integer> exerciseIds, Principal principal) {
        assertCanModify(routineId, principal);
        return ResponseEntity.ok(routineExerciseService.assignExercisesToRoutine(routineId, exerciseIds).stream()
                .map(routineExerciseMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/routine/{routineId}/exercises")
    @Operation(summary = "List the exercises of a routine")
    @PreAuthorize("hasAuthority('LIST_ROUTINE_EXERCISE')")
    public ResponseEntity<List<RoutineExerciseResponse>> getByRoutine(@PathVariable Integer routineId, Principal principal) {
        assertCanView(routineId, principal);
        return ResponseEntity.ok(routineExerciseService.getByRoutineId(routineId).stream()
                .map(routineExerciseMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @PutMapping("/routine/{routineId}/exercises")
    @Operation(summary = "Replace the exercises of a routine", description = "Adds, updates and removes exercises so the routine matches the list")
    @PreAuthorize("hasAuthority('EDIT_ROUTINE_EXERCISE')")
    public ResponseEntity<List<RoutineExerciseResponse>> replaceExercises(@PathVariable Integer routineId,
                                                                          @RequestBody List<RoutineExerciseRequest> items,
                                                                          Principal principal) {
        assertCanModify(routineId, principal);
        User user = userService.getUserByEmail(principal.getName());
        List<RoutineExercise> entities = items.stream()
                .map(item -> {
                    item.setRoutineId(routineId);
                    return routineExerciseMapper.toEntity(item);
                })
                .collect(Collectors.toList());
        return ResponseEntity.ok(routineExerciseService.replaceExercises(routineId, entities, user).stream()
                .map(routineExerciseMapper::toResponse)
                .collect(Collectors.toList()));
    }
}
