package tn.esprit.user.services.Interfaces;

import jakarta.mail.MessagingException;
import tn.esprit.user.dtos.LoginDTO;
import tn.esprit.user.dtos.RecoverPasswordDTO;
import tn.esprit.user.entities.Role;
import tn.esprit.user.entities.User;
import tn.esprit.user.security.Response;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.http.ResponseEntity;

import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.util.List;

public interface IAuthService {
    ResponseEntity<Response> saveUser(User user, String userAgent);

    ResponseEntity<?> confirmDevice(String userAgent, HttpServletResponse response, LoginDTO loginDTO, Integer code);

    ResponseEntity<?> loginUser(LoginDTO loginDTO, HttpServletResponse response, HttpServletRequest request, String userAgent);
    ResponseEntity<?> loginTFA(LoginDTO loginDTO,HttpServletResponse response, int verificationCode,String userAgent);

    void logout(HttpServletResponse response);

    ResponseEntity<Response> verifyAccount(String code);

    ResponseEntity<Boolean> isAuthenticated(Principal principal);

    ResponseEntity<List<Role>> getRole(Principal principal);

    ResponseEntity<Response> forgotPassword(String email) throws MessagingException, UnsupportedEncodingException;

    ResponseEntity<Response> recoverPassword(String token, RecoverPasswordDTO passwordDTO);

    void disableTwoFactorAuth(String email);

    ResponseEntity<?> enableTwoFactorAuth(String email, String verificationCode);

    ResponseEntity<?> generateTwoFactorAuthQrCode(String email);

    boolean verifyTwoFactorAuth(String email, int verificationCode);
}
