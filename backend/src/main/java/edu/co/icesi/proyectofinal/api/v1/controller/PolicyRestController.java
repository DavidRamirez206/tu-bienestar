package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.PolicyRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.PolicyResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.PolicyMapper;
import edu.co.icesi.proyectofinal.entity.Policy;
import edu.co.icesi.proyectofinal.services.PolicyService;
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
@RequestMapping("/api/v1/policies")
@Tag(name = "Policies", description = "Endpoints for managing security policies")
public class PolicyRestController {

    private final PolicyService policyService;
    private final PolicyMapper policyMapper;

    public PolicyRestController(PolicyService policyService, PolicyMapper policyMapper) {
        this.policyService = policyService;
        this.policyMapper = policyMapper;
    }

    @GetMapping
    @Operation(
            summary = "Get all policies",
            description = "Retrieves a list of all policies in the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of policies retrieved successfully")
            }
    )
    @PreAuthorize("hasAuthority('LIST_POLICY')")
    public ResponseEntity<List<PolicyResponse>> getAll() {
        return ResponseEntity.ok(policyService.getPolicies().stream()
                .map(policyMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get policy by ID",
            description = "Retrieves a single policy's details by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Policy found", 
                            content = @Content(schema = @Schema(implementation = PolicyResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Policy not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('LIST_POLICY')")
    public ResponseEntity<PolicyResponse> getById(@PathVariable Long id) {
        return ResponseEntity.ok(policyMapper.toResponse(policyService.getPolicyById(id)));
    }

    @PostMapping
    @Operation(
            summary = "Create a policy",
            description = "Creates a new policy with the provided data",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Policy created", 
                            content = @Content(schema = @Schema(implementation = PolicyResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('CREATE_POLICY')")
    public ResponseEntity<PolicyResponse> create(@RequestBody PolicyRequest request) {
        Policy policy = policyMapper.toEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(policyMapper.toResponse(policyService.savePolicy(policy)));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a policy",
            description = "Updates an existing policy's details by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Policy updated successfully", 
                            content = @Content(schema = @Schema(implementation = PolicyResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Policy not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('EDIT_POLICY')")
    public ResponseEntity<PolicyResponse> update(@PathVariable Long id, @RequestBody PolicyRequest request) {
        Policy policy = policyMapper.toEntity(request);
        policy.setId(id);
        return ResponseEntity.ok(policyMapper.toResponse(policyService.updatePolicy(policy.getId(), policy)));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a policy",
            description = "Deletes a policy from the system by its ID",
            responses = {
                    @ApiResponse(responseCode = "244", description = "Policy deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "Policy not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('DELETE_POLICY')")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        policyService.deletePolicyById(id);
        return ResponseEntity.noContent().build();
    }
}
