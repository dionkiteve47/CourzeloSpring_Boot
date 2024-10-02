package tn.esprit.user.controllers;

import tn.esprit.user.dto.schedule.TimeTableDTO;
import tn.esprit.user.entities.schedule.ElementModule;
import tn.esprit.user.repositories.ElementModuleRepository;
import tn.esprit.user.services.Implementations.TimeTableService;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import jakarta.validation.Valid;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@RestController
@Slf4j
@RequestMapping(value = "/api/TimeTable", produces = MediaType.APPLICATION_JSON_VALUE)
public class TimeTableController {

    private final TimeTableService timeTableService;
    private final ElementModuleRepository elementModuleRepository;

    @Autowired
    public TimeTableController(TimeTableService timeTableService, ElementModuleRepository elementModuleRepository) {
        this.timeTableService = timeTableService;
        this.elementModuleRepository = elementModuleRepository;
    }
    @PostMapping
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> create(@Valid @RequestBody TimeTableDTO timeTableDTO) {
        String timeTableId = timeTableService.create(timeTableDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(timeTableId);
    }
    //newTimetable
    @GetMapping
    public List<Map<String, List<ElementModule>>> getAllEmplois() {
        return timeTableService.getAllEmplois();
    }
    //newTimetable
    @GetMapping("/{id}")
    public List<ElementModule> getEmploisByClasse(@PathVariable String id) {
        return timeTableService.getEmploisByClasse(id);
    }
    //newTimetable
    @GetMapping("/generate")
    public List<Map<String, List<ElementModule>>> generateEmplois() {
        return timeTableService.generateEmplois();
    }
    //newTimetable
    //getEmploiByProf
    @GetMapping("/prof/{id}")
    public  List<ElementModule>getEmploiByProf(@PathVariable String id) {

        return timeTableService.getEmploiByProf(id);
    }
    @PutMapping("/{id}")
    public ResponseEntity<String> update(@PathVariable String id, @Valid @RequestBody TimeTableDTO timeTableDTO) {
        timeTableService.update(id, timeTableDTO);
        return ResponseEntity.ok("TimeTable updated successfully");
    }
    @DeleteMapping("/{id}")
    @ApiResponse(responseCode = "201")
    public ResponseEntity<String> delete(@PathVariable String id) {
        timeTableService.delete(id);
        return ResponseEntity.ok("TimeTable deleted successfully");
    }
    @GetMapping("/count")
    public ResponseEntity<Long> countTimetables() {
        long timetableCount = timeTableService.countTimetables();
        return ResponseEntity.ok(timetableCount);
    }
}
