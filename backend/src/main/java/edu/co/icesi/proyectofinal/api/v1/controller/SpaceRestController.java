package edu.co.icesi.proyectofinal.api.v1.controller;

import edu.co.icesi.proyectofinal.api.v1.dto.SpaceRequest;
import edu.co.icesi.proyectofinal.api.v1.dto.SpaceResponse;
import edu.co.icesi.proyectofinal.api.v1.mapper.SpaceMapper;
import edu.co.icesi.proyectofinal.services.SpaceService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
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
@RequestMapping("/api/v1/spaces")
@Tag(name = "Spaces", description = "Espacios deportivos donde ocurren los eventos")
public class SpaceRestController {

    private final SpaceService spaceService;
    private final SpaceMapper spaceMapper;

    public SpaceRestController(SpaceService spaceService, SpaceMapper spaceMapper) {
        this.spaceService = spaceService;
        this.spaceMapper = spaceMapper;
    }

    @GetMapping
    @PreAuthorize("hasAuthority('LIST_SPACE')")
    public ResponseEntity<List<SpaceResponse>> getAll() {
        return ResponseEntity.ok(spaceService.findAll().stream().map(spaceMapper::toResponse).toList());
    }

    @GetMapping("/{id}")
    @PreAuthorize("hasAuthority('LIST_SPACE')")
    public ResponseEntity<SpaceResponse> getById(@PathVariable Integer id) {
        return ResponseEntity.ok(spaceMapper.toResponse(spaceService.findById(id)));
    }

    @PostMapping
    @PreAuthorize("hasAuthority('CREATE_SPACE')")
    public ResponseEntity<SpaceResponse> create(@Valid @RequestBody SpaceRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(spaceMapper.toResponse(spaceService.save(spaceMapper.toEntity(request))));
    }

    @PutMapping("/{id}")
    @PreAuthorize("hasAuthority('EDIT_SPACE')")
    public ResponseEntity<SpaceResponse> update(@PathVariable Integer id, @Valid @RequestBody SpaceRequest request) {
        return ResponseEntity.ok(spaceMapper.toResponse(spaceService.update(id, spaceMapper.toEntity(request))));
    }

    @DeleteMapping("/{id}")
    @PreAuthorize("hasAuthority('DELETE_SPACE')")
    public ResponseEntity<Void> delete(@PathVariable Integer id) {
        spaceService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
