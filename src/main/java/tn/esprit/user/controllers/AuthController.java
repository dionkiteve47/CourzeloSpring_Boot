package tn.esprit.user.controllers;

import tn.esprit.user.dtos.LoginDTO;
import tn.esprit.user.dtos.RecoverPasswordDTO;
import tn.esprit.user.dtos.RegisterDTO;
import tn.esprit.user.entities.Role;
import tn.esprit.user.entities.User;
import tn.esprit.user.security.Response;
import tn.esprit.user.services.Interfaces.IAuthService;
import io.github.resilience4j.ratelimiter.annotation.RateLimiter;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
@RequestMapping("/api/v1/auth")
@RestController
@RequiredArgsConstructor
@PreAuthorize("permitAll()")
@RateLimiter(name = "backend")
public class AuthController {
    private final IAuthService iAuthService;
    @Autowired
    private ModelMapper modelMapper;

    @PostMapping("/confirmDevice/{code}")
    public ResponseEntity<?> confirmDevice(@Valid @RequestBody LoginDTO loginDTO, @PathVariable Integer code, HttpServletResponse response, @RequestHeader(value = "User-Agent") String userAgent) {
        return iAuthService.confirmDevice(userAgent, response, loginDTO, code);
    }

    @PostMapping("/signing")
    public ResponseEntity<?> authenticateUser(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response, HttpServletRequest request, @RequestHeader(value = "User-Agent") String userAgent) {
        return iAuthService.loginUser(loginDTO, response, request, userAgent);
    }
    @PostMapping("/signingTFA")
    public ResponseEntity<?> authenticateTFAUser(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response,@RequestParam String verificationCode, @RequestHeader(value = "User-Agent") String userAgent) {
        return iAuthService.loginTFA(loginDTO, response, Integer.parseInt(verificationCode),userAgent);
    }

    @PostMapping("/signup")
    public ResponseEntity<Response> signup(@Valid @RequestBody RegisterDTO user, @RequestHeader(value = "User-Agent") String userAgent) {
        User user1 = new User(user.getEmail(),
                user.getPassword(),
                user.getName(),
                user.getLastname()
        );
        return iAuthService.saveUser(user1, userAgent);
    }

    @PostMapping("/logout")
    @PreAuthorize("isAuthenticated()")
    public void logout(HttpServletResponse response ,Principal principal) {
        iAuthService.logout(response,principal);
    }

    @GetMapping("/verify")
    @CacheEvict(value = {"UsersList", "MyInfo", "AnotherCache"}, allEntries = true)
    public ResponseEntity<Response> verifyAccount(@RequestParam("code") String code) {
        return iAuthService.verifyAccount(code);
    }

    @GetMapping("/isAuthenticated")
    public ResponseEntity<Boolean> isAuthenticated(Principal principal) {

        return iAuthService.isAuthenticated(principal);
    }

    @GetMapping("/getRole")
    public ResponseEntity<List<Role>> getRole(Principal principal) {
        return iAuthService.getRole(principal);
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<Response> forgotPassword(@RequestParam("email") String email) throws MessagingException, UnsupportedEncodingException {
        return iAuthService.forgotPassword(email);
    }

    @PostMapping("/recover-password")
    @CacheEvict(value = {"UsersList", "MyInfo", "AnotherCache"}, allEntries = true)
    public ResponseEntity<Response> recoverPassword(@RequestParam("token") String token, @RequestBody RecoverPasswordDTO passwordDTO) {
        return iAuthService.recoverPassword(token, passwordDTO);
    }
    @PostMapping("/generateTwoFactorAuthQrCode")
    public ResponseEntity<?> generateTwoFactorAuthQrCode(Principal principal) {
        return iAuthService.generateTwoFactorAuthQrCode(principal.getName());
    }

    @PostMapping("/enableTwoFactorAuth")
    @CacheEvict(value = {"UsersList", "MyInfo", "AnotherCache"}, allEntries = true)
    public ResponseEntity<?> enableTwoFactorAuth(Principal principal, @RequestParam String verificationCode) {
        return iAuthService.enableTwoFactorAuth(principal.getName(), verificationCode);
    }

    @PostMapping("/disableTwoFactorAuth")
    @CacheEvict(value = {"UsersList", "MyInfo", "AnotherCache"}, allEntries = true)
    public void disableTwoFactorAuth(Principal principal) {
        iAuthService.disableTwoFactorAuth(principal.getName());
    }

    @PostMapping("/verifyTwoFactorAuth")
    public boolean verifyTwoFactorAuth(@RequestParam String email, @RequestParam int verificationCode) {
        return iAuthService.verifyTwoFactorAuth(email, verificationCode);
    }
}
