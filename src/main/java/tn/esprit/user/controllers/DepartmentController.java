package tn.esprit.user.controllers;

import tn.esprit.user.dto.schedule.DepartmentDTO;
import tn.esprit.user.repositories.DepartmentRepository;
import tn.esprit.user.services.Implementations.DepartmentService;
import tn.esprit.user.services.Implementations.FieldOfStudyService;
import tn.esprit.user.utils.NotFoundException;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping(value = "/api/departments", produces = MediaType.APPLICATION_JSON_VALUE)
public class DepartmentController {
    private final DepartmentService departmentService;
    private final FieldOfStudyService fieldOfStudyService;
    private final DepartmentRepository departmentRepository;

    public DepartmentController(final DepartmentService departementService, FieldOfStudyService fieldOfStudyService, DepartmentRepository departmentRepository) {
        this.departmentService = departementService;
        this.fieldOfStudyService = fieldOfStudyService;
        this.departmentRepository = departmentRepository;
    }

    @GetMapping
    public ResponseEntity<List<DepartmentDTO>> getAllDepartments() {
        return ResponseEntity.ok(departmentService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<DepartmentDTO> getDepartment(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(departmentService.get(id));
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createFieldOfStudy(
            @RequestBody @Valid final DepartmentDTO departmentDTO) {
        final String createdId = departmentService.create(departmentDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }


    @PostMapping("/{id}")
    public ResponseEntity<String> updateDepartment(@PathVariable String id,
                                                   @RequestBody @Valid final DepartmentDTO departmentDTO) {
        departmentService.update(id, departmentDTO);
        return ResponseEntity.ok(id);
    }

    @CrossOrigin(origins = "http://localhost:4200")
    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteDepartment(@PathVariable(name = "id") final String id) {
        departmentService.delete(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/count")
    public ResponseEntity<Long> countDepartments() {
        long departmentCount = departmentService.countDepartements();
        return ResponseEntity.ok(departmentCount);
    }

    @GetMapping("/search")
    public ResponseEntity<List<DepartmentDTO>> searchDepartments(@RequestParam String name) {
        try {
            List<DepartmentDTO> searchedDepartments = departmentService.searchDepartments(name);
            return new ResponseEntity<>(searchedDepartments, HttpStatus.OK);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/{id}/fields")
    public ResponseEntity<List<DepartmentDTO>> getFields(@PathVariable String id) {
        try {
            List<DepartmentDTO> departments = departmentService.getDepartmentsByFieldOfStudy(id);
            return new ResponseEntity<>(departments, HttpStatus.OK);
        } catch (NotFoundException ex) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

}
