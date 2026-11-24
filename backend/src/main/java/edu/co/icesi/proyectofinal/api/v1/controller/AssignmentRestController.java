package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.AssignmentRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.AssignmentResponse;
import edu.co.icesi.proyectofinal.api.v1.dto.UserResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.AssignmentMapper;
import edu.co.icesi.proyectofinal.api.v1.mapper.UserMapper;
import edu.co.icesi.proyectofinal.entity.Assignment;
import edu.co.icesi.proyectofinal.services.AssignmentService;
import edu.co.icesi.proyectofinal.services.UserService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/assignments")
@Tag(name = "Assignments", description = "Asignacion de entrenadores a los usuarios")
public class AssignmentRestController {

    private final AssignmentService assignmentService;
    private final AssignmentMapper assignmentMapper;
    private final UserService userService;
    private final UserMapper userMapper;

    public AssignmentRestController(AssignmentService assignmentService, AssignmentMapper assignmentMapper,
                                    UserService userService, UserMapper userMapper) {
        this.assignmentService = assignmentService;
        this.assignmentMapper = assignmentMapper;
        this.userService = userService;
        this.userMapper = userMapper;
    }

    /** El administrador ve todas; el entrenador sus alumnos; los demas su propio entrenador. */
    @GetMapping
    @PreAuthorize("hasAuthority('LIST_ASSIGNMENT')")
    public ResponseEntity<List<AssignmentResponse>> getAll(Authentication authentication) {
        List<Assignment> assignments;
        if (isAdmin(authentication)) {
            assignments = assignmentService.findAll();
        } else if (isTrainer(authentication)) {
            assignments = assignmentService.findByTrainerId(currentUserId(authentication));
        } else {
            assignments = assignmentService.findByUserId(currentUserId(authentication));
        }
        return ResponseEntity.ok(toResponses(assignments));
    }

    /** "Mis alumnos": los usuarios asignados al entrenador que hace la peticion. */
    @GetMapping("/me")
    @PreAuthorize("hasAuthority('LIST_ASSIGNMENT')")
    public ResponseEntity<List<AssignmentResponse>> getMyStudents(Authentication authentication) {
        return ResponseEntity.ok(toResponses(assignmentService.findByTrainerId(currentUserId(authentication))));
    }

    @GetMapping("/trainer/{trainerId}")
    @PreAuthorize("hasAuthority('LIST_ASSIGNMENT')")
    public ResponseEntity<List<AssignmentResponse>> getByTrainer(@PathVariable Integer trainerId,
                                                                 Authentication authentication) {
        requireOwnerOrAdmin(trainerId, authentication);
        return ResponseEntity.ok(toResponses(assignmentService.findByTrainerId(trainerId)));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('LIST_ASSIGNMENT')")
    public ResponseEntity<List<AssignmentResponse>> getByUser(@PathVariable Integer userId,
                                                              Authentication authentication) {
        requireOwnerOrAdmin(userId, authentication);
        return ResponseEntity.ok(toResponses(assignmentService.findByUserId(userId)));
    }

    @GetMapping("/{userId}/{trainerId}")
    @PreAuthorize("hasAuthority('LIST_ASSIGNMENT')")
    public ResponseEntity<AssignmentResponse> getById(@PathVariable Integer userId, @PathVariable Integer trainerId,
                                                      Authentication authentication) {
        if (!isAdmin(authentication) && !currentUserId(authentication).equals(userId)) {
            requireOwnerOrAdmin(trainerId, authentication);
        }
        return ResponseEntity.ok(assignmentMapper.toResponse(assignmentService.findById(userId, trainerId)));
    }

    /** Entrenadores disponibles para el panel administrativo. */
    @GetMapping("/trainers")
    @PreAuthorize("hasAuthority('CREATE_ASSIGNMENT')")
    public ResponseEntity<List<UserResponse>> getTrainers(Authentication authentication) {
        requireAdmin(authentication);
        return ResponseEntity.ok(assignmentService.findUsersByRole("TRAINER").stream()
                .map(userMapper::toResponse).toList());
    }

    /** Usuarios que pueden recibir un entrenador en el panel administrativo. */
    @GetMapping("/trainees")
    @PreAuthorize("hasAuthority('CREATE_ASSIGNMENT')")
    public ResponseEntity<List<UserResponse>> getTrainees(Authentication authentication) {
        requireAdmin(authentication);
        return ResponseEntity.ok(assignmentService.findUsersByRole("TRAINEE").stream()
                .map(userMapper::toResponse).toList());
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_ASSIGNMENT')")
    public ResponseEntity<AssignmentResponse> create(@Valid @RequestBody AssignmentRequest request,
                                                     Authentication authentication) {
        requireAdmin(authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(assignmentMapper.toResponse(assignmentService.assign(request.getUserId(), request.getTrainerId())));
    }

    /** Reasigna el usuario al entrenador indicado; el entrenador anterior deja de verlo. */
    @PutMapping
    @PreAuthorize("hasAuthority('CREATE_ASSIGNMENT')")
    public ResponseEntity<AssignmentResponse> reassign(@Valid @RequestBody AssignmentRequest request,
                                                       Authentication authentication) {
        requireAdmin(authentication);
        return ResponseEntity.ok(
                assignmentMapper.toResponse(assignmentService.reassign(request.getUserId(), request.getTrainerId())));
    }

    @DeleteMapping("/{userId}/{trainerId}")
    @PreAuthorize("hasAuthority('DELETE_ASSIGNMENT')")
    public ResponseEntity<Void> delete(@PathVariable Integer userId, @PathVariable Integer trainerId,
                                       Authentication authentication) {
        requireAdmin(authentication);
        assignmentService.unassign(userId, trainerId);
        return ResponseEntity.noContent().build();
    }

    private List<AssignmentResponse> toResponses(List<Assignment> assignments) {
        return assignments.stream().map(assignmentMapper::toResponse).toList();
    }

    private void requireAdmin(Authentication authentication) {
        if (!isAdmin(authentication)) {
            throw new AccessDeniedException("Only administrators can manage trainer assignments");
        }
    }

    private void requireOwnerOrAdmin(Integer userId, Authentication authentication) {
        if (!isAdmin(authentication) && !currentUserId(authentication).equals(userId)) {
            throw new AccessDeniedException("You can only see your own assignments");
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return hasRole(authentication, "ADMIN");
    }

    private boolean isTrainer(Authentication authentication) {
        return hasRole(authentication, "TRAINER");
    }

    private boolean hasRole(Authentication authentication, String role) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> role.equals(authority.getAuthority()));
    }

    private Integer currentUserId(Authentication authentication) {
        return userService.getUserByEmail(authentication.getName()).getIdUser();
    }
}
