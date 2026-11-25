package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.RecommendationRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.RecommendationResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.RecommendationMapper;
import edu.co.icesi.proyectofinal.entity.Recommendation;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.services.RecommendationService;
import edu.co.icesi.proyectofinal.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/recommendations")
@Tag(name = "Recommendations", description = "Endpoints for managing trainer recommendations")
public class RecommendationRestController {

    private final RecommendationService recommendationService;
    private final RecommendationMapper recommendationMapper;
    private final UserService userService;

    public RecommendationRestController(RecommendationService recommendationService, 
                                      RecommendationMapper recommendationMapper,
                                      UserService userService) {
        this.recommendationService = recommendationService;
        this.recommendationMapper = recommendationMapper;
        this.userService = userService;
    }

    @GetMapping
    @Operation(
            summary = "Get all recommendations",
            description = "Retrieves a list of all recommendations in the system (filtered for trainers/trainees)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of recommendations retrieved successfully")
            }
    )
    @PreAuthorize("hasAuthority('LIST_RECOMMENDATION')")
    public ResponseEntity<List<RecommendationResponse>> getAll(Principal principal) {
        User user = userService.getUserByEmail(principal.getName());
        boolean isAdmin = user.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equals("ADMIN"));

        List<Recommendation> recommendations;
        if (isAdmin) {
            recommendations = recommendationService.getRecommendations();
        } else {
            // Combine recommendations where user is trainer OR trainee into a new mutable list
            recommendations = new java.util.ArrayList<>(recommendationService.getByUserId(user.getIdUser()));
            recommendations.addAll(recommendationService.getByTrainerId(user.getIdUser()));
        }

        return ResponseEntity.ok(recommendations.stream()
                .distinct()
                .map(recommendationMapper::toResponse)
                .collect(Collectors.toList()));
    }

    @GetMapping("/{id}")
    @Operation(
            summary = "Get recommendation by ID",
            description = "Retrieves a single recommendation's details by its ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recommendation found", 
                            content = @Content(schema = @Schema(implementation = RecommendationResponse.class))),
                    @ApiResponse(responseCode = "404", description = "Recommendation not found", content = @Content)
            }
    )
    @PreAuthorize("hasAnyAuthority('LIST_RECOMMENDATION', 'VIEW_OWN_PROFILE')")
    public ResponseEntity<RecommendationResponse> getById(@PathVariable Integer id, Principal principal) {
        User currentUser = userService.getUserByEmail(principal.getName());
        Recommendation rec = recommendationService.getRecommendation(id);

        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equals("ADMIN"));
        
        if (!isAdmin && !rec.getTrainer().getIdUser().equals(currentUser.getIdUser()) 
                && !rec.getUser().getIdUser().equals(currentUser.getIdUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        return ResponseEntity.ok(recommendationMapper.toResponse(rec));
    }

    @PostMapping
    @Operation(
            summary = "Create a recommendation",
            description = "Creates a new recommendation with the provided data",
            responses = {
                    @ApiResponse(responseCode = "201", description = "Recommendation created", 
                            content = @Content(schema = @Schema(implementation = RecommendationResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('CREATE_RECOMMENDATION')")
    public ResponseEntity<RecommendationResponse> create(@RequestBody RecommendationRequest request) {
        Recommendation recommendation = recommendationMapper.toEntity(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(recommendationMapper.toResponse(recommendationService.createRecommendation(recommendation)));
    }

    @PutMapping("/{id}")
    @Operation(
            summary = "Update a recommendation",
            description = "Updates an existing recommendation's details (Admin or Author only)",
            responses = {
                    @ApiResponse(responseCode = "200", description = "Recommendation updated successfully", 
                            content = @Content(schema = @Schema(implementation = RecommendationResponse.class))),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Recommendation not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('EDIT_RECOMMENDATION')")
    public ResponseEntity<RecommendationResponse> update(@PathVariable Integer id, @RequestBody RecommendationRequest request, Principal principal) {
        User currentUser = userService.getUserByEmail(principal.getName());
        Recommendation existing = recommendationService.getRecommendation(id);

        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equals("ADMIN"));

        if (!isAdmin && !existing.getTrainer().getIdUser().equals(currentUser.getIdUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        Recommendation recommendation = recommendationMapper.toEntity(request);
        recommendation.setIdRecommendation(id);
        return ResponseEntity.ok(recommendationMapper.toResponse(recommendationService.updateRecommendation(recommendation)));
    }

    @DeleteMapping("/{id}")
    @Operation(
            summary = "Delete a recommendation",
            description = "Deletes a recommendation from the system (Admin or Author only)",
            responses = {
                    @ApiResponse(responseCode = "244", description = "Recommendation deleted successfully"),
                    @ApiResponse(responseCode = "403", description = "Access denied"),
                    @ApiResponse(responseCode = "404", description = "Recommendation not found", content = @Content)
            }
    )
    @PreAuthorize("hasAuthority('DELETE_RECOMMENDATION')")
    public ResponseEntity<Void> delete(@PathVariable Integer id, Principal principal) {
        User currentUser = userService.getUserByEmail(principal.getName());
        Recommendation existing = recommendationService.getRecommendation(id);

        boolean isAdmin = currentUser.getUserRoles().stream()
                .anyMatch(ur -> ur.getRole().getName().equals("ADMIN"));

        if (!isAdmin && !existing.getTrainer().getIdUser().equals(currentUser.getIdUser())) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).build();
        }

        recommendationService.deleteRecommendation(id);
        return ResponseEntity.noContent().build();
    }
}
