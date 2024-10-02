package tn.esprit.user.controllers;

import tn.esprit.user.dto.program.ClassDTO;
import tn.esprit.user.dtos.UserListDTO;
import tn.esprit.user.entities.schedule.SemesterNumber;
import tn.esprit.user.services.Interfaces.IClassService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import org.modelmapper.ModelMapper;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/api/v1/class")
@RestController
@PreAuthorize("hasRole('ADMIN')")
@RateLimiter(name = "backend")
public class ClassController {
    private final IClassService iClassService;
    private ModelMapper modelMapper;

    public ClassController(IClassService iClassService, ModelMapper modelMapper) {
        this.iClassService = iClassService;
        this.modelMapper = modelMapper;
    }

    @PostMapping("/add")
    public ResponseEntity<Boolean> addClass(ClassDTO classDTO) {
        return iClassService.addClass(classDTO);
    }

    @GetMapping("/all")
    public ResponseEntity<List<ClassDTO>> getClasses() {
        return iClassService.getClasses();
    }
   /* @GetMapping("/allWithoutPagination")
    public ResponseEntity<List<ClassDTO>> getClassesWithoutPagination() {
        return iClassService.getClassesWithoutPagination();
    }*/
    
    @GetMapping("/myClass")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<ClassDTO> getMyClass(Principal principal) {
        return iClassService.getMyClass(principal);
    }

    @DeleteMapping("/delete/{classID}")
    @CacheEvict(value = {"ProgramClasses"}, allEntries = true)
    public ResponseEntity<Boolean> deleteClass(@PathVariable String classID) {
        return iClassService.deleteClass(classID);
    }

    @PostMapping("/update")
    @CacheEvict(value = {"ProgramClasses"}, allEntries = true)
    public ResponseEntity<Boolean> updateClass(@RequestBody ClassDTO classDTO) {
        return iClassService.updateClass(classDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/getClassUsers/{role}")
    public ResponseEntity<UserListDTO> getClassUsers(@RequestParam(required = false) String classID,
                                                     Principal principal,
                                                     @PathVariable String role,
                                                     @RequestParam(defaultValue = "0") int page,
                                                     @RequestParam(defaultValue = "2") int sizePerPage) {
        return iClassService.getClassUsers(classID, principal, role, page, sizePerPage);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/add/user/{userEmail}/{role}")
    public ResponseEntity<Boolean> addUserToClass(@RequestParam String id, @PathVariable String userEmail, @PathVariable String role) {
        return iClassService.addUserToClass(id, userEmail, role);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/remove/user/{userEmail}")
    public ResponseEntity<Boolean> removeUser(@RequestParam String classID, @PathVariable String userEmail) {
        return iClassService.removeUser(classID, userEmail);
    }

    @GetMapping("/searchBySemester")
    public List<ClassDTO> searchClassesBySemester(@RequestParam SemesterNumber semesterNumber) {
        return iClassService.searchClassesBySemester(semesterNumber)
                .stream()
                .map(classes -> modelMapper.map(classes, ClassDTO.class))
                .toList();
    }

}
