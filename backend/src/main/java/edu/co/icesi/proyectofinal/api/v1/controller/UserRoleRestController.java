package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.UserRoleRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.UserRoleResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.UserRoleMapper;
import edu.co.icesi.proyectofinal.entity.UserRole;
import edu.co.icesi.proyectofinal.entity.keys.UserRoleId;
import edu.co.icesi.proyectofinal.services.UserRoleService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/user-roles")
@Tag(name = "User Roles", description = "Endpoints for managing user role assignments")
public class UserRoleRestController {

    private final UserRoleService userRoleService;
    private final UserRoleMapper userRoleMapper;

    public UserRoleRestController(UserRoleService userRoleService, UserRoleMapper userRoleMapper) {
        this.userRoleService = userRoleService;
        this.userRoleMapper = userRoleMapper;
    }

    @GetMapping
    @Operation(
            summary = "Get all user role assignments",
            description = "Retrieves a list of all user role assignments in the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of user role assignments retrieved successfully")
            }
    )
    @PreAuthorize("hasAuthority('LIST_USER_ROLE')")
    public ResponseEntity<List<UserRoleResponse>> getAll() {
        return ResponseEntity.ok(userRoleService.findAll().stream()
                .map(userRoleMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{userId}/{roleId}")
    @Operation(
            summary = "Get user role assignment by user ID and role ID",
            description = "Retrieves a single user role assignment's details by its user ID and role ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User role assignment found", 
                            content = @Content(schema = @Schema(implementation = UserRoleResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User role assignment not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('LIST_USER_ROLE')")
    public ResponseEntity<UserRoleResponse> getById(@PathVariable Integer userId, @PathVariable Long roleId) {
        UserRoleId id = new UserRoleId();
        id.setUserId(userId);
        id.setRoleId(roleId);
        return ResponseEntity.ok(userRoleMapper.toResponse(userRoleService.getById(id)));
    }

    @PostMapping
    @Operation(
            summary = "Create a user role assignment",
            description = "Creates a new user role assignment with the provided data",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User role assignment created", 
                            content = @Content(schema = @Schema(implementation = UserRoleResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('CREATE_USER_ROLE')")
    public ResponseEntity<UserRoleResponse> create(@RequestBody UserRoleRequest request) {
        UserRole userRole = userRoleMapper.toEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(userRoleMapper.toResponse(userRoleService.save(userRole)));
    }

    @PutMapping("/{userId}/{roleId}")
    @Operation(
            summary = "Update a user role assignment",
            description = "Updates an existing user role assignment's details by its user ID and role ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User role assignment updated successfully", 
                            content = @Content(schema = @Schema(implementation = UserRoleResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User role assignment not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('EDIT_USER_ROLE')")
    public ResponseEntity<UserRoleResponse> update(@PathVariable Integer userId, @PathVariable Long roleId, @RequestBody UserRoleRequest request) {
        UserRoleId id = new UserRoleId();
        id.setUserId(userId);
        id.setRoleId(roleId);
        
        UserRole userRole = userRoleMapper.toEntity(request);
        return ResponseEntity.ok(userRoleMapper.toResponse(userRoleService.update(id, userRole)));
    }

    @DeleteMapping("/{userId}/{roleId}")
    @Operation(
            summary = "Delete a user role assignment",
            description = "Deletes a user role assignment from the system by its user ID and role ID",
            responses = {
                    @ApiResponse(responseCode = "244", description = "User role assignment deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User role assignment not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('DELETE_USER_ROLE')")
    public ResponseEntity<Void> delete(@PathVariable Integer userId, @PathVariable Long roleId) {
        UserRoleId id = new UserRoleId();
        id.setUserId(userId);
        id.setRoleId(roleId);
        userRoleService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
