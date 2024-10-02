package com.courzelo.lms.services.user;

import tn.esprit.user.entities.User;
import tn.esprit.user.entities.VerificationToken;
import tn.esprit.user.repositories.UserRepository;
import tn.esprit.user.repositories.VerificationTokenRepository;
import tn.esprit.user.security.Response;
import tn.esprit.user.security.jwt.JWTUtils;
import tn.esprit.user.services.Implementations.AuthService;
import tn.esprit.user.services.Implementations.EmailService;
import tn.esprit.user.services.Interfaces.IDeviceMetadataService;
import tn.esprit.user.utils.CookieUtil;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.context.SecurityContextHolder;

import java.io.UnsupportedEncodingException;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceTest {
    @InjectMocks
    private AuthService authService;
    @Mock
    private CookieUtil cookieUtil;
    @Mock
    private UserRepository userRepository;

    @Mock
    private VerificationTokenRepository verificationTokenRepository;

    @Mock
    private IDeviceMetadataService iDeviceMetadataService;

    @Mock
    private EmailService emailService;
    @Mock
    private JWTUtils jwtUtils;
    private static final Logger log = LoggerFactory.getLogger(AuthServiceTest.class);
    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        when(cookieUtil.createAccessTokenCookie(anyString(), anyLong())).thenReturn(ResponseCookie.from("accessToken", "cookieValue").build());
        when(cookieUtil.createRefreshTokenCookie(anyString(), anyLong())).thenReturn(ResponseCookie.from("refreshToken", "cookieValue").build());
    }
    @Test
    void saveUserShouldReturnBadRequestWhenEmailExists() {
        User user = new User();
        user.setEmail("test@test.com");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(true);

        ResponseEntity<Response> response = authService.saveUser(user, "userAgent");

        assertEquals(HttpStatus.BAD_REQUEST, response.getStatusCode());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
    }

    @Test
    void saveUserShouldReturnOkWhenUserIsSavedSuccessfully() throws MessagingException, UnsupportedEncodingException {
        User user = new User();
        user.setEmail("test@test.com");

        when(userRepository.existsByEmail(user.getEmail())).thenReturn(false);
        when(userRepository.save(any(User.class))).thenReturn(user);

        ResponseEntity<Response> response = authService.saveUser(user, "userAgent");

        assertEquals(HttpStatus.OK, response.getStatusCode());
        verify(userRepository, times(1)).existsByEmail(user.getEmail());
        verify(userRepository, times(1)).save(any(User.class));
        verify(iDeviceMetadataService, times(1)).saveDeviceDetails(anyString(), any(User.class));
        verify(emailService, times(1)).sendVerificationEmail(any(User.class), any(VerificationToken.class));
    }
    @Test
    void refreshTokenCreatesNewAccessToken() {
        String email = "test@test.com";
        HttpServletResponse response = mock(HttpServletResponse.class);

        authService.refreshToken(response, email);

        verify(response, times(1)).addHeader(eq(HttpHeaders.SET_COOKIE), anyString());
    }

    @Test
    void refreshTokenLogsCorrectMessages() {
        String email = "test@test.com";
        HttpServletResponse response = mock(HttpServletResponse.class);

        authService.refreshToken(response, email);

        verify(log, times(1)).info("refreshToken :Refreshing Token...");
        verify(log, times(1)).info("refreshToken :Access token created!");
        verify(log, times(1)).info("refreshToken :Refreshing token DONE!");
    }
}