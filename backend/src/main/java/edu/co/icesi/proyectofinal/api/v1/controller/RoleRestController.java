package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.RoleRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.RoleResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.RoleMapper;
import edu.co.icesi.proyectofinal.entity.Role;
import edu.co.icesi.proyectofinal.services.RoleService;
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
@RequestMapping("/api/v1/roles")
@Tag(name = "Roles", description = "Endpoints for managing user roles")
public class RoleRestController {

    private final RoleService roleService;
    private final RoleMapper roleMapper;

    public RoleRestController(RoleService roleService, RoleMapper roleMapper) {
        this.roleService = roleService;
        this.roleMapper = roleMapper;
    }

    @GetMapping
    @Operation(
            summary = "Get all roles",
            description = "Retrieves a list of all roles in the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of roles retrieved successfully")
            }
    )
    @PreAuthorize("hasAuthority('LIST_ROLE')")
    public ResponseEntity<List<RoleResponse>> getAll() {
        return ResponseEntity.ok(roleService.getRoles().stream()
                .map(roleMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get role by ID",
            description = "Retrieves a single role's details by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role found", 
                            content = @Content(schema = @Schema(implementation = RoleResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('LIST_ROLE')")
    public ResponseEntity<RoleResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(roleMapper.toResponse(roleService.getRoleById(id)));
    }

    @PostMapping
    @Operation(
            summary = "Create a new role",
            description = "Creates a new role with the provided data",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Role created", 
                            content = @Content(schema = @Schema(implementation = RoleResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('CREATE_ROLE')")
    public ResponseEntity<RoleResponse> create(@RequestBody RoleRequest request) {
        Role role = roleMapper.toEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(roleMapper.toResponse(roleService.saveRole(role)));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update an existing role",
            description = "Updates an existing role's details by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role updated successfully", 
                            content = @Content(schema = @Schema(implementation = RoleResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('EDIT_ROLE')")
    public ResponseEntity<RoleResponse> update(
            @PathVariable Long id,
            @RequestBody RoleRequest request) {

        Role updated = roleService.updateRole(
                id,
                request.getName(),
                request.getPolicyIds()
        );

        return ResponseEntity.ok(roleMapper.toResponse(updated));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a role",
            description = "Deletes a role from the system by its ID",
            responses = {
                    @ApiResponse(responseCode = "244", description = "Role deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Role not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('DELETE_ROLE')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        roleService.deleteRoleById(id);
        return ResponseEntity.noContent().build();
    }
}
