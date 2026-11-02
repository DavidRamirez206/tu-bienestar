package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.RoutineRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.RoutineResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.RoutineMapper;
import edu.co.icesi.proyectofinal.entity.Routine;
import edu.co.icesi.proyectofinal.services.RoutineService;
import edu.co.icesi.proyectofinal.services.UserService;
import edu.co.icesi.proyectofinal.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/routines")
@Tag(name = "Routines", description = "Endpoints for managing workout routines")
public class RoutineRestController {

    private final RoutineService routineService;
    private final RoutineMapper routineMapper;
    private final UserService userService;

    public RoutineRestController(RoutineService routineService, RoutineMapper routineMapper, UserService userService) {
        this.routineService = routineService;
        this.routineMapper = routineMapper;
        this.userService = userService;
    }

    private static final Set<String> VALID_LEVELS = Set.of("Principiante", "Intermedio", "Avanzado");

    private void applyPublicationRules(Routine routine, Authentication authentication) {
        if (!routine.isPredesigned()) {
            routine.setLevel(null);
            return;
        }
        boolean canPublish = authentication.getAuthorities().stream()
                .anyMatch(authority -> Set.of("TRAINER", "ADMIN").contains(authority.getAuthority()));
        if (!canPublish) {
            throw new AccessDeniedException("Only trainers can publish predesigned routines");
        }
        if (routine.getLevel() == null || !VALID_LEVELS.contains(routine.getLevel())) {
            throw new IllegalArgumentException("Invalid routine: level is required for predesigned routines");
        }
    }

    @Operation(
            summary = "Get routine by ID",
            description = "Retrieves a single routine's details by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Routine found", 
                            content = @Content(schema = @Schema(implementation = RoutineResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Routine not found", content = @Content)
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LIST_ROUTINE')")
    public ResponseEntity<RoutineResponse> getById(@PathVariable Integer id, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        Routine routine = routineService.findById(id);
        
        if (!routineService.canView(routine, user)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }
        
        return ResponseEntity.ok(routineMapper.toResponse(routine));
    }

    @Operation(
            summary = "Get all routines",
            description = "Retrieves a list of all routines in the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of routines retrieved successfully")
            }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('LIST_ROUTINE')")
    public ResponseEntity<List<RoutineResponse>> getAll(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());

        List<Routine> myRoutines = routineService.findByUserId(user.getIdUser());
        List<Routine> predesignedRoutines = routineService.findPredesigned();

        List<Routine> combinedRoutines = new ArrayList<>(myRoutines);
        for (Routine routine : predesignedRoutines) {
            if (combinedRoutines.stream().noneMatch(r -> r.getIdRoutine().equals(routine.getIdRoutine()))) {
                combinedRoutines.add(routine);
            }
        }

        List<RoutineResponse> routineResponses = combinedRoutines.stream()
                .map(routineMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(routineResponses);
    }

    @Operation(
            summary = "Create a new routine",
            description = "Creates a new routine with the provided data",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Routine created", 
                            content = @Content(schema = @Schema(implementation = RoutineResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)
            }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_ROUTINE')")
    public ResponseEntity<RoutineResponse> create(@Valid @RequestBody RoutineRequest routineRequest, Authentication principal) {
        User user = userService.getUserByEmail(principal.getName());
        Routine routine = routineMapper.toEntity(routineRequest);
        routine.setUserRoutine(user);
        applyPublicationRules(routine, principal);
        Routine savedRoutine = routineService.save(routine);
        return ResponseEntity.status(HttpStatus.CREATED).body(routineMapper.toResponse(savedRoutine));
    }

    @Operation(
            summary = "Update an existing routine",
            description = "Updates an existing routine's details by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Routine updated successfully", 
                            content = @Content(schema = @Schema(implementation = RoutineResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Routine not found", content = @Content)
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_ROUTINE')")
    public ResponseEntity<RoutineResponse> update(@PathVariable Integer id, @Valid @RequestBody RoutineRequest routineRequest, Authentication principal) {
        User user = userService.getUserByEmail(principal.getName());
        routineService.assertCanModify(id, user);
        Routine routine = routineMapper.toEntity(routineRequest);
        applyPublicationRules(routine, principal);
        Routine updatedRoutine = routineService.update(id, routine);
        return ResponseEntity.ok(routineMapper.toResponse(updatedRoutine));
    }

    @PostMapping("/{id}/adopt")
    @Operation(summary = "Adopt a predesigned routine", description = "Copies the routine into the user's routines")
    @PreAuthorize("hasAuthority('CREATE_ROUTINE')")
    public ResponseEntity<RoutineResponse> adopt(@PathVariable Integer id, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.status(HttpStatus.CREATED).body(routineMapper.toResponse(routineService.adoptRoutine(id, user)));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a routine",
            description = "Deletes a routine from the system by its ID",
            responses = {
                    @ApiResponse(responseCode = "244", description = "Routine deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Routine not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('DELETE_ROUTINE')")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id, Principal principal) {
        routineService.assertCanModify(id, userService.getUserByEmail(principal.getName()));
        routineService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
