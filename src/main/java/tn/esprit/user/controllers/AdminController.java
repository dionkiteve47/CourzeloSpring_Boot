package tn.esprit.user.controllers;

import tn.esprit.user.dtos.UserDTO;
import tn.esprit.user.dtos.UserListDTO;
import tn.esprit.user.security.JwtResponse;
import tn.esprit.user.security.Response;
import tn.esprit.user.services.Interfaces.IAdminService;
import tn.esprit.user.services.Implementations.UserService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/api/v1/admin")
@RestController
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPERADMIN')")
@RateLimiter(name = "backend")
public class AdminController {
    private final IAdminService iAdminService;
    private final UserService userService;

    @GetMapping("/users")
    @Cacheable(value = "UsersList", key = "#page + '-' + #sizePerPage")
    public ResponseEntity<UserListDTO> getUsers(@RequestParam(defaultValue = "0") int page,
                                                @RequestParam(defaultValue = "2") int sizePerPage) {
        return iAdminService.getUsers(page, sizePerPage);
    }
    @GetMapping("/userInfo")
    public ResponseEntity<UserDTO> getUserInfo(@RequestParam String email) {
        return ResponseEntity.ok()
                .body(userService.getMyInfo(email));
    }

    @PostMapping("/add/{userID}/{role}")
    @CacheEvict(value = {"UsersList", "MyInfo", "AnotherCache"}, allEntries = true)
    public ResponseEntity<Response> addRole(@PathVariable String userID, @PathVariable String role) {
        return iAdminService.addRole(role, userID);
    }

    @PostMapping("/remove/{userID}/{role}")
    @CacheEvict(value = {"UsersList", "MyInfo", "AnotherCache"}, allEntries = true)
    public ResponseEntity<Response> removeRole(@PathVariable String userID, @PathVariable String role) {
        return iAdminService.removeRole(role, userID);
    }

    @PostMapping("/ban/{userID}")
    @CacheEvict(value = {"UsersList", "MyInfo", "AnotherCache"}, allEntries = true)
    public ResponseEntity<Response> toggleBan(@PathVariable String userID) {
        return iAdminService.toggleBan(userID);
    }

    @PostMapping("/enable/{userID}")
    @CacheEvict(value = {"UsersList", "MyInfo", "AnotherCache"}, allEntries = true)
    public ResponseEntity<Response> toggleEnable(@PathVariable String userID) {
        return iAdminService.toggleEnable(userID);
    }
}
