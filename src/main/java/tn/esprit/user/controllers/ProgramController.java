package tn.esprit.user.controllers;

import tn.esprit.user.dto.program.ClassDTO;
import tn.esprit.user.dto.program.ClassListDTO;
import tn.esprit.user.dto.program.ProgramDTO;
import tn.esprit.user.dto.program.ProgramListDTO;
import tn.esprit.user.services.Interfaces.IProgramService;
import com.fasterxml.jackson.core.JsonProcessingException;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;

@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/api/v1/program")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('ADMIN')")
@RateLimiter(name = "backend")
public class ProgramController {
    private final IProgramService iProgramService;

    @PostMapping("/add")
    @CacheEvict(value = {"ProgramList"}, allEntries = true)
    public ResponseEntity<Boolean> addProgram(Principal principal, @RequestBody ProgramDTO programDTO) {
        return iProgramService.addProgram(principal, programDTO);
    }
    @GetMapping("/get/{classID}")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<ProgramDTO> getProgramByClassID(@PathVariable String classID){
        return iProgramService.getProgramByClassID(classID);
    }
    @GetMapping("/myPrograms")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProgramListDTO> getMyPrograms(Principal principal) {
        return iProgramService.getMyPrograms(principal.getName());
    }
    @GetMapping("/get/ProgramSuggestion")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ProgramDTO> getProgramSuggestions(Principal principal) throws JsonProcessingException {
        return iProgramService.getProgramSuggestions(principal.getName());
    }


    @GetMapping("/all")
    @Cacheable(value = "ProgramList", key = "#page + '-' + #sizePerPage")
    public ResponseEntity<ProgramListDTO> getPrograms(Principal principal, @RequestParam(defaultValue = "0") int page,
                                                      @RequestParam(defaultValue = "2") int sizePerPage) {
        return iProgramService.getPrograms(principal, page, sizePerPage);
    }

    @DeleteMapping("/delete/{programID}")
    @CacheEvict(value = {"ProgramList"}, allEntries = true)
    public ResponseEntity<Boolean> deleteProgram(Principal principal, @PathVariable String programID) {
        return iProgramService.deleteProgram(principal, programID);
    }

    @PostMapping("/update")
    @CacheEvict(value = {"ProgramList"}, allEntries = true)
    public ResponseEntity<Boolean> updateProgram(Principal principal, @RequestBody ProgramDTO programDTO) {
        return iProgramService.updateProgram(principal, programDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getProgramClasses")
    @Cacheable(value = "ProgramClasses", key = "#page + '-' + #sizePerPage + '-' + #principal.name")
    public ResponseEntity<ClassListDTO> getProgramClasses(Principal principal,
                                                          @RequestParam() String program,
                                                          @RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "2") int sizePerPage) {

        return iProgramService.getProgramClasses(principal, program, page, sizePerPage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add/class")
    @CacheEvict(value = {"ProgramClasses"}, allEntries = true)
    public ResponseEntity<Boolean> addUserToInstitution(@RequestParam() String program, @RequestBody ClassDTO classe, Principal principal) {
        return iProgramService.addClassToProgram(program, classe, principal);
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/join")
    public ResponseEntity<HttpStatus> joinProgram(Principal principal, @RequestParam() String key){
        return iProgramService.joinProgram(principal.getName(), key);
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/joinByID")
    public ResponseEntity<HttpStatus> joinProgramByID(Principal principal, @RequestParam() String id){
        return iProgramService.joinProgramByID(principal.getName(), id);
    }
    @PreAuthorize("isAuthenticated()")
    @PostMapping("/leave")
    public ResponseEntity<HttpStatus> leaveProgram(Principal principal, @RequestParam() String program){
        return iProgramService.leaveProgram(principal.getName(), program);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/remove/class/{classID}")
    @CacheEvict(value = {"ProgramClasses"}, allEntries = true)
    public ResponseEntity<Boolean> removeClass(@PathVariable String classID, Principal principal) {
        return iProgramService.removeClass(classID, principal);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/predictPopularity")
    public ResponseEntity<String> predictPopularity(@RequestParam() String programID) throws JsonProcessingException {
        return iProgramService.predictPopularity(programID);
    }
}
