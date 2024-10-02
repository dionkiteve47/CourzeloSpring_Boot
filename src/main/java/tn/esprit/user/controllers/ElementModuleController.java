package tn.esprit.user.controllers;

import tn.esprit.user.dto.schedule.ElementModuleDTO;
import tn.esprit.user.entities.schedule.ElementModule;
import tn.esprit.user.entities.schedule.Period;
import tn.esprit.user.services.Implementations.ElementModuleService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.persistence.PostPersist;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")

@RestController
@RequestMapping(value = "/api/elementModules", produces = MediaType.APPLICATION_JSON_VALUE)
public class ElementModuleController {
    private final ElementModuleService elementModuleService;

    public ElementModuleController(final ElementModuleService elementModuleService) {
        this.elementModuleService = elementModuleService;
    }
    @PostMapping("/create")
    public ResponseEntity<ElementModuleDTO> createElementModuleTest(@RequestBody @Valid ElementModuleDTO elementModuleDTO) {
        ElementModuleDTO createdElementModule = elementModuleService.createElementModule(elementModuleDTO);
        return new ResponseEntity<>(createdElementModule, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<ElementModuleDTO>> getAllElementModules() {
        return ResponseEntity.ok(elementModuleService.findAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<ElementModuleDTO> getElementModule(
            @PathVariable(name = "id") final String id) {
        return ResponseEntity.ok(elementModuleService.get(id));
    }

  /*  @PostMapping
    @PostPersist
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createElementModule(
            @RequestBody @Valid final ElementModuleDTO elementModuleDTO) {
        final String createdId = elementModuleService.create(elementModuleDTO);
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }*/


    @PutMapping("/{id}")
    public ResponseEntity<String> updateElementModule(@PathVariable(name = "id") final String id,
                                                      @RequestBody @Valid final ElementModuleDTO elementModuleDTO) {
        elementModuleService.update(id, elementModuleDTO);
        return ResponseEntity.ok(id);
    }

    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "204")
    public ResponseEntity<Void> deleteElementModule(@PathVariable(name = "id") final String id) {
        elementModuleService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // @RequestMapping("/api/days")
   /* @GetMapping
    public ResponseEntity<DayOfWeek[]> getDaysOfWeek() {
        return ResponseEntity.ok(DayOfWeek.values());
    }*/
    @PostMapping
    @PostPersist
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> createElementModule1(
            @RequestBody @Valid final ElementModuleDTO elementModuleDTO) {
        final String createdId = String.valueOf(elementModuleService.create(elementModuleDTO));
        return new ResponseEntity<>(createdId, HttpStatus.CREATED);
    }

    @GetMapping("/enums")
    public ResponseEntity<Period[]> getEnums() {
        return ResponseEntity.ok(Period.values());
    }

}
