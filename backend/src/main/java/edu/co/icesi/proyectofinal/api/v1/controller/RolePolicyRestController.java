package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.RolePolicyRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.RolePolicyResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.RolePolicyMapper;
import edu.co.icesi.proyectofinal.entity.RolePolicy;
import edu.co.icesi.proyectofinal.entity.keys.RolePoliciesId;
import edu.co.icesi.proyectofinal.services.RolePolicyService;
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
@RequestMapping("/api/v1/role-policies")
@Tag(name = "Role Policies", description = "Endpoints for managing role-policy permissions")
public class RolePolicyRestController {

    private final RolePolicyService rolePolicyService;
    private final RolePolicyMapper rolePolicyMapper;

    public RolePolicyRestController(RolePolicyService rolePolicyService, RolePolicyMapper rolePolicyMapper) {
        this.rolePolicyService = rolePolicyService;
        this.rolePolicyMapper = rolePolicyMapper;
    }

    @GetMapping
    @Operation(
            summary = "Get all role-policy assignments",
            description = "Retrieves a list of all role-policy assignments in the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of role-policy assignments retrieved successfully")
            }
    )
    @PreAuthorize("hasAuthority('LIST_ROLE_POLICY')")
    public ResponseEntity<List<RolePolicyResponse>> getAll() {
        return ResponseEntity.ok(rolePolicyService.findAll().stream()
                .map(rolePolicyMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{roleId}/{policyId}")
    @Operation(
            summary = "Get role-policy assignment by IDs",
            description = "Retrieves a single role-policy assignment's details by its role ID and policy ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role-policy assignment found", 
                            content = @Content(schema = @Schema(implementation = RolePolicyResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Role-policy assignment not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('LIST_ROLE_POLICY')")
    public ResponseEntity<RolePolicyResponse> getById(@PathVariable Long roleId, @PathVariable Long policyId) {
        RolePoliciesId id = new RolePoliciesId();
        id.setRoleId(roleId);
        id.setPolicyId(policyId);
        return ResponseEntity.ok(rolePolicyMapper.toResponse(rolePolicyService.getById(id)));
    }

    @PostMapping
    @Operation(
            summary = "Create a role-policy assignment",
            description = "Creates a new role-policy assignment with the provided data",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Role-policy assignment created", 
                            content = @Content(schema = @Schema(implementation = RolePolicyResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('CREATE_ROLE_POLICY')")
    public ResponseEntity<RolePolicyResponse> create(@RequestBody RolePolicyRequest request) {
        RolePolicy rolePolicy = rolePolicyMapper.toEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(rolePolicyMapper.toResponse(rolePolicyService.save(rolePolicy)));
    }

    @PutMapping("/{roleId}/{policyId}")
    @Operation(
            summary = "Update a role-policy assignment",
            description = "Updates an existing role-policy assignment's details by its role ID and policy ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Role-policy assignment updated successfully", 
                            content = @Content(schema = @Schema(implementation = RolePolicyResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Role-policy assignment not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('EDIT_ROLE_POLICY')")
    public ResponseEntity<RolePolicyResponse> update(@PathVariable Long roleId, @PathVariable Long policyId, @RequestBody RolePolicyRequest request) {
        RolePoliciesId id = new RolePoliciesId();
        id.setRoleId(roleId);
        id.setPolicyId(policyId);
        
        RolePolicy rolePolicy = rolePolicyMapper.toEntity(request);
        return ResponseEntity.ok(rolePolicyMapper.toResponse(rolePolicyService.update(id, rolePolicy)));
    }

    @DeleteMapping("/{roleId}/{policyId}")
    @Operation(
            summary = "Delete a role-policy assignment",
            description = "Deletes a role-policy assignment from the system by its role ID and policy ID",
            responses = {
                    @ApiResponse(responseCode = "244", description = "Role-policy assignment deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Role-policy assignment not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('DELETE_ROLE_POLICY')")
    public ResponseEntity<Void> delete(@PathVariable Long roleId, @PathVariable Long policyId) {
        RolePoliciesId id = new RolePoliciesId();
        id.setRoleId(roleId);
        id.setPolicyId(policyId);
        rolePolicyService.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
