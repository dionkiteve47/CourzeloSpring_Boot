package tn.esprit.user.controllers;


import tn.esprit.user.dto.schedule.FieldOfStudyDTO;
import tn.esprit.user.entities.schedule.Semester;
import tn.esprit.user.repositories.DepartmentRepository;
import tn.esprit.user.services.Implementations.FieldOfStudyService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping(value = "/api/fieldOfStudies")
public class FieldOfStudyController {

    private final FieldOfStudyService fieldOfStudyService;
    private final DepartmentRepository departmentRepository;

    public FieldOfStudyController(final FieldOfStudyService fieldOfStudyService, DepartmentRepository departmentRepository) {
        this.fieldOfStudyService = fieldOfStudyService;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<FieldOfStudyDTO>> getAllFieldOfStudies() {
        return ResponseEntity.ok(fieldOfStudyService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<FieldOfStudyDTO> getFieldOfStudy(
            @PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(fieldOfStudyService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<HttpStatus> createFieldOfStudy(
            @RequestBody @Valid final FieldOfStudyDTO fieldOfStudyDTO) {
        final String createdId = fieldOfStudyService.create(fieldOfStudyDTO);
        return new ResponseEntity<>(HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<String> updateFieldOfStudy(@PathVariable(name = "id") final String id,
                                                     @RequestBody @Valid final FieldOfStudyDTO fieldOfStudyDTO) {
        fieldOfStudyService.update(id, fieldOfStudyDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteFieldOfStudy(@PathVariable(name = "id") final String id) {
        fieldOfStudyService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @GetMapping("/{fieldOfStudyId}/semesters")
    public List<Semester> getSemestersByFieldOfStudy(@PathVariable String id) {
        return fieldOfStudyService.getSemestersByFieldOfStudy(id);
    }

}
