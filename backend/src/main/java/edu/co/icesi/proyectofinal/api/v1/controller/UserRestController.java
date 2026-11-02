package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.UserRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.UserResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.UserMapper;
import edu.co.icesi.proyectofinal.entity.User;
import edu.co.icesi.proyectofinal.services.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.security.access.prepost.PreAuthorize;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/users")
@Tag(name = "Users", description = "Endpoints for managing system users")
public class UserRestController {

    private final UserService userService;
    private final UserMapper userMapper;

    public UserRestController(UserService userService, UserMapper userMapper) {
        this.userService = userService;
        this.userMapper = userMapper;
    }

    @Operation(
            summary = "Get user by ID",
            description = "Retrieves a single user's details by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User found", 
                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @GetMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('LIST_USER', 'VIEW_OWN_PROFILE')")
    public ResponseEntity<UserResponse> getById(@PathVariable Integer id) {
        User user = userService.getUserById(id);
        return ResponseEntity.ok(userMapper.toResponse(user));
    }

    @Operation(
            summary = "Get all users",
            description = "Retrieves a list of all users in the system",
            responses = {
                    @ApiResponse(responseCode = "200", description = "List of users retrieved successfully")
            }
    )
    @GetMapping
    @PreAuthorize("hasAuthority('LIST_USER')")
    public ResponseEntity<List<UserResponse>> getAll() {
        List<User> users = userService.getUsers();
        List<UserResponse> userResponses = users.stream()
                .map(userMapper::toResponse)
                .collect(Collectors.toList());
        return ResponseEntity.ok(userResponses);
    }

    @Operation(
            summary = "Create a new user",
            description = "Creates a new user with the provided data",
            responses = {
                    @ApiResponse(responseCode = "201", description = "User created", 
                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "400", description = "Invalid data", content = @Content)
            }
    )
    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_USER')")
    public ResponseEntity<UserResponse> create(@RequestBody UserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);
        User savedUser = userService.saveUser(user);
        return ResponseEntity.status(HttpStatus.CREATED).body(userMapper.toResponse(savedUser));
    }

    @Operation(
            summary = "Update an existing user",
            description = "Updates an existing user's details by their ID",
            responses = {
                    @ApiResponse(responseCode = "200", description = "User updated successfully", 
                            content = @Content(schema = @Schema(implementation = UserResponse.class))),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyAuthority('EDIT_USER', 'EDIT_OWN_PROFILE')")
    public ResponseEntity<UserResponse> update(@PathVariable Integer id, @RequestBody UserRequest userRequest) {
        User user = userMapper.toEntity(userRequest);
        User updatedUser = userService.updateUser(id, user);
        return ResponseEntity.ok(userMapper.toResponse(updatedUser));
    }

    @Operation(
            summary = "Delete a user",
            description = "Deletes a user from the system by their ID",
            responses = {
                    @ApiResponse(responseCode = "244", description = "User deleted successfully"),
                    @ApiResponse(responseCode = "404", description = "User not found", content = @Content)
            }
    )
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_USER')")
    public ResponseEntity<Void> deleteById(@PathVariable Integer id) {
        userService.deleteUserById(id);
        return ResponseEntity.noContent().build();
    }
}
