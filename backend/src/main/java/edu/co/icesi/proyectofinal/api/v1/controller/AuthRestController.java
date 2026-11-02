package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.AuthRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.AuthResponse;
import edu.co.icesi.proyectofinal.api.v1.dto.UserResponse;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.security.CustomUserDetailService;
import edu.co.icesi.proyectofinal.security.JwtService;
import edu.co.icesi.proyectofinal.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@Tag(name = "Authentication", description = "Endpoints for user login")
public class AuthRestController {

    private final JwtService jwtService;
    private final CustomUserDetailService customUserDetailService;
    private final AuthenticationManager authenticationManager;
    private final UserService userService;

    public AuthRestController(JwtService jwtService,
                              CustomUserDetailService customUserDetailService,
                              AuthenticationManager authenticationManager,
                              UserService userService) {
        this.jwtService = jwtService;
        this.customUserDetailService = customUserDetailService;
        this.authenticationManager = authenticationManager;
        this.userService = userService;
    }

    @Operation(
            summary = "User Login",
            description = "Authenticates a user and returns a JWT token with user data",
            responses = {
                    @ApiResponse(
                            responseCode = "200",
                            description = "Successfully authenticated",
                            content = @Content(
                                    mediaType = "application/json",
                                    schema = @Schema(implementation = AuthResponse.class)
                            )
                    ),
                    @ApiResponse(responseCode = "401", description = "Invalid credentials", content = @Content)
            }
    )
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest authRequest) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword())
        );

        UserDetails appUser = customUserDetailService.loadUserByUsername(authRequest.getUsername());
        String token = jwtService.generateToken(appUser);

        User user = userService.getUserByEmail(authRequest.getUsername());
        UserResponse userResponse = new UserResponse(
                user.getIdUser(),
                user.getFirstName(),
                user.getLastName(),
                user.getInstitutionalEmail(),
                user.getAge()
        );

        return ResponseEntity.ok(new AuthResponse(token, userResponse));
    }
}
