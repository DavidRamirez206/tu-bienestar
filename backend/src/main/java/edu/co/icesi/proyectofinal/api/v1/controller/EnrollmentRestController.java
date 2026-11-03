package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.EnrollmentRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.EnrollmentResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.EnrollmentMapper;
import edu.co.icesi.proyectofinal.entity.Enrollment;
import edu.co.icesi.proyectofinal.services.EnrollmentService;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1/enrollments")
@Tag(name = "Enrollments", description = "Inscripciones de los usuarios a las actividades")
public class EnrollmentRestController {

    private final EnrollmentService enrollmentService;
    private final EnrollmentMapper enrollmentMapper;
    private final UserService userService;

    public EnrollmentRestController(EnrollmentService enrollmentService, EnrollmentMapper enrollmentMapper,
                                    UserService userService) {
        this.enrollmentService = enrollmentService;
        this.enrollmentMapper = enrollmentMapper;
        this.userService = userService;
    }

    /** El administrador ve todas; los demas solo las suyas. */
    @GetMapping
    @PreAuthorize("hasAuthority('LIST_ENROLLMENT')")
    public ResponseEntity<List<EnrollmentResponse>> getAll(Authentication authentication) {
        List<Enrollment> enrollments = isAdmin(authentication)
                ? enrollmentService.findAll()
                : enrollmentService.findByUserId(currentUserId(authentication));
        return ResponseEntity.ok(toResponses(enrollments));
    }

    @GetMapping("/me")
    @PreAuthorize("hasAuthority('LIST_ENROLLMENT')")
    public ResponseEntity<List<EnrollmentResponse>> getMine(Authentication authentication) {
        return ResponseEntity.ok(toResponses(enrollmentService.findByUserId(currentUserId(authentication))));
    }

    @GetMapping("/user/{userId}")
    @PreAuthorize("hasAuthority('LIST_ENROLLMENT')")
    public ResponseEntity<List<EnrollmentResponse>> getByUser(@PathVariable Integer userId, Authentication authentication) {
        requireOwnerOrAdmin(userId, authentication);
        return ResponseEntity.ok(toResponses(enrollmentService.findByUserId(userId)));
    }

    @GetMapping("/{userId}/{activityId}")
    @PreAuthorize("hasAuthority('LIST_ENROLLMENT')")
    public ResponseEntity<EnrollmentResponse> getById(@PathVariable Integer userId, @PathVariable Integer activityId,
                                                      Authentication authentication) {
        requireOwnerOrAdmin(userId, authentication);
        return ResponseEntity.ok(enrollmentMapper.toResponse(enrollmentService.findById(userId, activityId)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_ENROLLMENT')")
    public ResponseEntity<EnrollmentResponse> create(@Valid @RequestBody EnrollmentRequest request,
                                                     Authentication authentication) {
        Integer userId = request.getUserId() != null ? request.getUserId() : currentUserId(authentication);
        requireOwnerOrAdmin(userId, authentication);
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(enrollmentMapper.toResponse(enrollmentService.enroll(userId, request.getActivityId())));
    }

    @DeleteMapping("/{userId}/{activityId}")
    @PreAuthorize("hasAuthority('DELETE_ENROLLMENT')")
    public ResponseEntity<Void> cancel(@PathVariable Integer userId, @PathVariable Integer activityId,
                                       Authentication authentication) {
        requireOwnerOrAdmin(userId, authentication);
        enrollmentService.cancel(userId, activityId);
        return ResponseEntity.noContent().build();
    }

    private List<EnrollmentResponse> toResponses(List<Enrollment> enrollments) {
        return enrollments.stream().map(enrollmentMapper::toResponse).toList();
    }

    private void requireOwnerOrAdmin(Integer userId, Authentication authentication) {
        if (!isAdmin(authentication) && !currentUserId(authentication).equals(userId)) {
            throw new AccessDeniedException("You can only manage your own enrollments");
        }
    }

    private boolean isAdmin(Authentication authentication) {
        return authentication.getAuthorities().stream()
                .anyMatch(authority -> "ADMIN".equals(authority.getAuthority()));
    }

    private Integer currentUserId(Authentication authentication) {
        return userService.getUserByEmail(authentication.getName()).getIdUser();
    }
}
