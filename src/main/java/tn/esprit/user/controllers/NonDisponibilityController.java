package tn.esprit.user.controllers;


import tn.esprit.user.dto.schedule.NonDisponibilityDTO;
import tn.esprit.user.services.Implementations.NonDisponibilityService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping(value = "/api/nonDisponibilities")
public class NonDisponibilityController {

    private final NonDisponibilityService nonDisponibilityService;

    public NonDisponibilityController(final NonDisponibilityService nonDisponibilityService) {
        this.nonDisponibilityService = nonDisponibilityService;
    }

    @GetMapping
    public ResponseEntity<List<NonDisponibilityDTO>> getAllNonDisponibilities() {
        return ResponseEntity.ok(nonDisponibilityService.findAll());
    }


    @GetMapping("/{id}")
    public ResponseEntity<NonDisponibilityDTO> getNonDisponibility(
            @PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(nonDisponibilityService.get(String.valueOf(id)));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    @PreAuthorize("hasRole('TEACHER')")
    public ResponseEntity<HttpStatus> createNonDisponibility(
            @RequestBody @Valid final NonDisponibilityDTO nonDisponibilityDTO, Principal principal) {
        final String createdId = nonDisponibilityService.create(nonDisponibilityDTO,principal.getName());
        return new ResponseEntity<HttpStatus>( HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateNonDisponibility(@PathVariable(name = "id") final String id,
                                                         @RequestBody @Valid final NonDisponibilityDTO nonDisponibilityDTO) {
        nonDisponibilityService.update(id, nonDisponibilityDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteNonDisponibility(@PathVariable(name = "id") final String id) {
        nonDisponibilityService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
