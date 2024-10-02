package tn.esprit.user.controllers;

import tn.esprit.user.dto.program.ClassDTO;
import tn.esprit.user.dto.schedule.ModulDTO;
import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.services.Interfaces.IClassService;
import tn.esprit.user.services.Implementations.ModulService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@RestController
@RequestMapping(value = "/api/Modules", produces = MediaType.APPLICATION_JSON_VALUE)
public class ModulController {

    private final ModulService modulService;
    private final IClassService iClassService;

    public ModulController(final ModulService modulService, IClassService iClassService) {
        this.modulService = modulService;
        this.iClassService = iClassService;
    }

    @GetMapping
    public ResponseEntity<List<ModulDTO>> getAllModuls() {
        return ResponseEntity.ok(modulService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ModulDTO> getModul(@PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(modulService.get(id));
    }
    @PostMapping("/create")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> create(@RequestBody @Valid final ModulDTO modulDTO) {
        return new ResponseEntity<>(modulService.create(modulDTO), HttpStatus.CREATED);
    }

    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<ModulDTO> createModul(@RequestBody @Valid final ModulDTO modulDTO) {
        if (modulDTO.getAClass() == null) {
            Class aClass = new Class();
            // Set default values for the ClassDTO object if needed
            aClass.setName("Default Class Name");
            modulDTO.setAClass(aClass);
        }
        final ModulDTO createdModul = modulService.createModul(modulDTO);
        return new ResponseEntity<>(createdModul, HttpStatus.CREATED);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> updateModul(@PathVariable(name = "id") final String id,
                                              @RequestBody @Valid final ModulDTO modulDTO) {
        ModulDTO existingModul = modulService.get(id);
        if (existingModul == null) {
            return new ResponseEntity<>("Modul not found for ID: " + id, HttpStatus.NOT_FOUND);
        }
        modulService.update(id, modulDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteModul(@PathVariable(name = "id") final String id) {
        modulService.delete(id);
        return ResponseEntity.noContent().build();
    }
    @PostMapping("/addmodules")
    public ResponseEntity<Boolean> addClass(@RequestBody ClassDTO classDTO) {
        return iClassService.addClass1(classDTO);
    }

}
