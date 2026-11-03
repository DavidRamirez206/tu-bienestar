package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.ProgressRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.ProgressResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.ProgressMapper;
import edu.co.icesi.proyectofinal.entity.Progress;
import edu.co.icesi.proyectofinal.services.ProgressService;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/progress")
@Tag(name = "Progress", description = "Endpoints for tracking user workout progress")
public class ProgressRestController {

    private final ProgressService progressService;
    private final ProgressMapper progressMapper;
    private final UserService userService;

    public ProgressRestController(ProgressService progressService, ProgressMapper progressMapper, 
                                UserService userService) {
        this.progressService = progressService;
        this.progressMapper = progressMapper;
        this.userService = userService;
    }

    @GetMapping
    @Operation(
            summary = "Get all progress entries",
            description = "Retrieves a list of all progress entries for the current user",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of progress entries retrieved successfully")
            }
    )
    @PreAuthorize("hasAnyAuthority('LIST_PROGRESS', 'VIEW_OWN_PROFILE')")
    public ResponseEntity<List<ProgressResponse>> getAll(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        return ResponseEntity.ok(progressService.getByUserId(user.getIdUser()).stream()
                .map(progressMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/user/{userId}")
    @Operation(
            summary = "Get progress entries for a specific user",
            description = "Retrieves a list of all progress entries for a specific user (Admin or owner; assigned Trainer in Sprint 1)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of progress entries retrieved successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied")
            }
    )
    @PreAuthorize("hasAnyAuthority('LIST_PROGRESS', 'TRAINER', 'ADMIN')")
    public ResponseEntity<List<ProgressResponse>> getByUserId(@PathVariable Integer userId, Principal principal) {
        User currentUser = userService.getUserByEmail(principal.getName());

        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equals("ADMIN"));
        
        // Sprint 1: permitir también al entrenador asignado (AssignmentService)

        if (!isAdmin && !currentUser.getIdUser().equals(userId)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(progressService.getByUserId(userId).stream()
                .map(progressMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get progress by ID",
            description = "Retrieves a single progress entry's details by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Progress found", 
                            content = @Content(schema = @Schema(implementation = ProgressResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Progress not found", content = @Content)
            }
    )
    @PreAuthorize("hasAnyAuthority('LIST_PROGRESS', 'VIEW_OWN_PROFILE')")
    public ResponseEntity<ProgressResponse> getById(@PathVariable Integer id, Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        Progress progress = progressService.getProgress(id);

        boolean isAdmin = user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equals("ADMIN"));

        if (!isAdmin && (progress.getUserProgress() == null || !progress.getUserProgress().getIdUser().equals(user.getIdUser()))) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(progressMapper.toResponse(progress));
    }


    @PostMapping
    @Operation(
            summary = "Create a progress entry",
            description = "Creates a new progress entry with the provided data",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Progress created", 
                            content = @Content(schema = @Schema(implementation = ProgressResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)
            }
    )
    @PreAuthorize("hasAnyAuthority('CREATE_PROGRESS', 'EDIT_OWN_PROFILE')")
    public ResponseEntity<ProgressResponse> create(@RequestBody @Valid ProgressRequest request) {
        Progress progress = progressMapper.toEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(progressMapper.toResponse(progressService.createProgress(progress)));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a progress entry",
            description = "Updates an existing progress entry's details by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Progress updated successfully", 
                            content = @Content(schema = @Schema(implementation = ProgressResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Progress not found", content = @Content)
            }
    )
    @PreAuthorize("hasAnyAuthority('EDIT_PROGRESS', 'EDIT_OWN_PROFILE')")
    public ResponseEntity<ProgressResponse> update(@PathVariable Integer id, @RequestBody @Valid ProgressRequest request) {
        Progress progress = progressMapper.toEntity(request);
        progress.setIdProgress(id);
        return ResponseEntity.ok(progressMapper.toResponse(progressService.updateProgress(progress)));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a progress entry",
            description = "Deletes a progress entry from the system by its ID",
            responses = {
                    @ApiResponse(responseCode = "244", description = "Progress deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Progress not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('DELETE_PROGRESS')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        progressService.deleteProgress(id);
        return ResponseEntity.noContent().build();
    }
}
