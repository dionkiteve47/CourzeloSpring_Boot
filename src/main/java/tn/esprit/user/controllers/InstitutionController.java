package tn.esprit.user.controllers;

import tn.esprit.user.dto.program.CalendarDTO;
import tn.esprit.user.dto.program.InstitutionDTO;
import tn.esprit.user.dto.program.InstitutionListDTO;
import tn.esprit.user.dto.program.InstitutionUsersCountDTO;
import tn.esprit.user.dtos.UserListDTO;
import tn.esprit.user.services.Interfaces.IInstitutionService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/api/v1/institution")
@RestController
@RequiredArgsConstructor
@RateLimiter(name = "backend")
public class InstitutionController {
    private final IInstitutionService iInstitutionService;

    @PreAuthorize("hasRole('SUPERADMIN')")
    @GetMapping("/all")
    @Cacheable(value = "InstitutionList", key = "#page + '-' + #sizePerPage")
    public ResponseEntity<InstitutionListDTO> getInstitutions(@RequestParam(defaultValue = "0") int page,
                                                              @RequestParam(defaultValue = "2") int sizePerPage) {
        return iInstitutionService.getInstitutions(page, sizePerPage);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @PostMapping("/add")
    @CacheEvict(value = "InstitutionList", allEntries = true)
    public ResponseEntity<Boolean> addInstitution(@RequestBody InstitutionDTO institutionDTO) {
        return iInstitutionService.addInstitution(institutionDTO);
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @GetMapping("/get/{institutionID}")
    public ResponseEntity<InstitutionDTO> getInstitutionByID(@PathVariable String institutionID) {
        return iInstitutionService.getInstitutionByID(institutionID);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'TEACHER')")
    @GetMapping("/getMyInstitution")
    @Cacheable(value = "MyInstitution", key = "#principal.name")
    public ResponseEntity<InstitutionDTO> getMyInstitution(Principal principal) {
        return iInstitutionService.getMyInstitution(principal);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/")
    public ResponseEntity<InstitutionDTO> getInstitutionForAdmin(Principal principal) {
        return iInstitutionService.getInstitution(principal.getName());
    }

    @PreAuthorize("hasRole('SUPERADMIN')")
    @DeleteMapping("/{institutionID}")
    @CacheEvict(value = "InstitutionList", allEntries = true)
    public ResponseEntity<Boolean> deleteInstitution(@PathVariable String institutionID) {
        return iInstitutionService.deleteInstitution(institutionID);
    }

    @PostMapping("/update")
    @CacheEvict(value = {"InstitutionList"}, allEntries = true)
    public ResponseEntity<Boolean> updateInstitution(@RequestBody InstitutionDTO institutionDTO) {
        return iInstitutionService.updateInstitution(institutionDTO);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/updateMine")
    @CacheEvict(value = "MyInstitution", key = "#principal.name")
    public ResponseEntity<Boolean> updateMyInstitution(@RequestBody InstitutionDTO institutionDTO, Principal principal) {
        return iInstitutionService.updateMyInstitution(institutionDTO, principal);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @GetMapping("/getInstitutionUsers/{role}")
    @Cacheable(value = "InstitutionUsers", key = "#page + '-' + #sizePerPage + '-' + #role")
    public ResponseEntity<UserListDTO> getInstitutionUsers(@RequestParam(required = false) String institutionID,
                                                           Principal principal,
                                                           @PathVariable String role,
                                                           @RequestParam(defaultValue = "0") int page,
                                                           @RequestParam(defaultValue = "2") int sizePerPage) {

        return iInstitutionService.getInstitutionUsers(institutionID, principal, role, page, sizePerPage);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @PostMapping("/add/user/{userEmail}/{role}")
    @CacheEvict(value = {"InstitutionUsers","UsersList", "MyInfo"}, allEntries = true, key = "#userEmail")
    public ResponseEntity<Boolean> addUserToInstitution(@RequestParam(required = false) String institutionID, @PathVariable String userEmail, @PathVariable String role, Principal principal) {
        return iInstitutionService.addUserToInstitution(institutionID, userEmail, role, principal);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @PostMapping("/remove/user/{institutionID}/{userEmail}")
    @CacheEvict(value = {"InstitutionUsers","UsersList", "MyInfo"}, allEntries = true, key = "#userEmail")
    public ResponseEntity<Boolean> removeUser(@PathVariable String institutionID, @PathVariable String userEmail, Principal principal) {
        return iInstitutionService.removeUser(institutionID, userEmail, principal);
    }

    @PreAuthorize("hasAnyRole('ADMIN', 'SUPERADMIN')")
    @PostMapping("/remove/user/{userEmail}")
    @CacheEvict(value = {"InstitutionUsers","UsersList", "MyInfo"}, allEntries = true, key = "#userEmail")
    public ResponseEntity<Boolean> removeUserFromInstitution(@RequestParam(required = false) String institutionID, @PathVariable String userEmail, Principal principal) {
        return iInstitutionService.removeUser(institutionID, userEmail, principal);
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/countUsers")
    public ResponseEntity<InstitutionUsersCountDTO> countUsers(Principal principal) {
        return iInstitutionService.countUsers(principal);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/saveLocation")
    @CacheEvict(value = "MyInstitution", allEntries = true)
    public ResponseEntity<Boolean> saveLocation(@RequestBody InstitutionDTO institutionDTO) {
        return iInstitutionService.saveLocation(institutionDTO);
    }
    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/generateExcel")
    @CacheEvict(value = "MyInstitution", allEntries = true)
    public ResponseEntity<HttpStatus> generateExcel(@RequestBody List<CalendarDTO> events,Principal principal) {
        return iInstitutionService.generateExcel(events,principal);
    }
    @PreAuthorize("hasAnyRole('ADMIN', 'STUDENT', 'TEACHER')")
    @GetMapping("/downloadExcel")
    public ResponseEntity<byte[]> downloadExcel(Principal principal) {
        return iInstitutionService.downloadExcel(principal);
    }
}
