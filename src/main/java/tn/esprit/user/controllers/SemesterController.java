package tn.esprit.user.controllers;


import tn.esprit.user.dto.schedule.SemesterDTO;
import tn.esprit.user.services.Implementations.SemesterService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RestController
@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@RequestMapping(value = "/api/semesters", produces = MediaType.APPLICATION_JSON_VALUE)
public class SemesterController {

    private final SemesterService semesterService;

    public SemesterController(final SemesterService semesterService) {
        this.semesterService = semesterService;
    }

    @GetMapping
    public ResponseEntity<List<SemesterDTO>> getAllSemesters() {
        return ResponseEntity.ok(semesterService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<SemesterDTO> getSemester(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(semesterService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createSemester(@RequestBody @Valid final SemesterDTO semesterDTO) {
        final String createdId = semesterService.create(semesterDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateSemester(@PathVariable(name = "id") final String id,
                                                 @RequestBody @Valid final SemesterDTO semesterDTO) {
        semesterService.update(id, semesterDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteSemester(@PathVariable(name = "id") final String id) {
        semesterService.delete(id);
        return ResponseEntity.noContent().build();
    }

}
