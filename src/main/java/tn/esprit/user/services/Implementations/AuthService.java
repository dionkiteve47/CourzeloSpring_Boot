package tn.esprit.user.services.Implementations;


import tn.esprit.user.dtos.DeviceResDTO;
import tn.esprit.user.dtos.LoginDTO;
import tn.esprit.user.dtos.QRCodeResponse;
import tn.esprit.user.dtos.RecoverPasswordDTO;
import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.institution.Institution;
import tn.esprit.user.entities.*;
import tn.esprit.user.exceptions.ClassNotFoundException;
import tn.esprit.user.exceptions.*;
import tn.esprit.user.repositories.ClassRepository;
import tn.esprit.user.repositories.InstitutionRepository;
import tn.esprit.user.repositories.UserRepository;
import tn.esprit.user.repositories.VerificationTokenRepository;
import tn.esprit.user.security.JwtResponse;
import tn.esprit.user.security.Response;
import tn.esprit.user.security.jwt.JWTUtils;
import tn.esprit.user.services.Interfaces.IAuthService;
import tn.esprit.user.services.Interfaces.IDeviceMetadataService;
import tn.esprit.user.services.Interfaces.IRefreshTokenService;
import tn.esprit.user.utils.CookieUtil;
import tn.esprit.user.utils.GeoIPService;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.WriterException;
import com.google.zxing.client.j2se.MatrixToImageWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.QRCodeWriter;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;
import com.warrenstrange.googleauth.GoogleAuthenticator;
import com.warrenstrange.googleauth.GoogleAuthenticatorKey;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import net.bytebuddy.utility.RandomString;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.Instant;
import java.util.*;

@RequiredArgsConstructor
@Service
@Slf4j
public class AuthService implements IAuthService {
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final IRefreshTokenService iRefreshTokenService;
    private final AuthenticationManager authenticationManager;
    private final InstitutionRepository institutionRepository;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ClassRepository classRepository;
    private final JWTUtils jwtUtils;
    private final CookieUtil cookieUtil;
    private final EmailService emailService;
    private final IDeviceMetadataService iDeviceMetadataService;
    private final GeoIPService geoIPService;
    @Value("${Security.app.jwtExpirationMs}")
    private long jwtExpirationMs;
    @Value("${Security.app.refreshExpirationMs}")
    private long refreshExpirationMs;
    @Value("${Security.app.refreshRememberMeExpirationMs}")
    private long refreshRememberMeExpirationMs;

    public ResponseEntity<?> loginUser(LoginDTO loginDTO, @NonNull HttpServletResponse response, @NonNull HttpServletRequest request, String userAgent) {
        log.info("Starting Logging in...");
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
            User checkUser = userRepository.findUserByEmail(loginDTO.getEmail());
            if(!checkUser.getSecurity().isTwoFactorAuthEnabled()) {
                String ip = iDeviceMetadataService.getIpAddressFromHeader(request);
                log.info("ip :" + request.getRemoteAddr());
                log.info("host :" + request.getRemoteHost());
                log.info("user :" + request.getRemoteUser());
                String city = geoIPService.cityName(ip);
                log.info("city :" + city);

                if (!iDeviceMetadataService.isNewDevice(userAgent, checkUser)) {
                    iDeviceMetadataService.updateDeviceLastLogin(userAgent, checkUser);
                    log.info("Finished Logging in...");
                    return authenticateUser(authentication, response, loginDTO);
                } else {
                    try {
                        Random random = new Random();
                        int verificationCode = random.nextInt(9000) + 1000;
                        VerificationToken verificationToken = new VerificationToken(
                                String.valueOf(verificationCode),
                                checkUser,
                                VerificationTokenType.DEVICE_VERIFICATION
                        );
                        verificationTokenRepository.save(verificationToken);
                        emailService.sendVerificationCode(checkUser, verificationToken);
                    } catch (MessagingException | UnsupportedEncodingException e) {
                        throw new RuntimeException(e);
                    }
                    log.info("Finished Logging in...");
                    return ResponseEntity.status(HttpStatus.OK).body(new DeviceResDTO(true));
                }
            }else{
                log.info("Two Factor Auth Enabled!");
                return ResponseEntity.status(HttpStatus.OK).body(new Response("Two Factor Authentication Required"));
            }
        } catch (DisabledException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response("Please verify your email"));
        } catch (LockedException e) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response("Your account has been banned"));
        } catch (AuthenticationException e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Incorrect email or password"));
        }
    }
    public ResponseEntity<?> loginTFA(LoginDTO loginDTO, @NonNull HttpServletResponse response, int verificationCode,String userAgent) {
        try {
            log.info("Starting TFA login for user: {}", loginDTO.getEmail());
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
            log.info("User authenticated, verifying TFA code");

            if(verifyTwoFactorAuth(loginDTO.getEmail(), verificationCode)) {
                log.info("TFA code verified, authenticating user");
                User user = userRepository.findUserByEmail(loginDTO.getEmail());
                iDeviceMetadataService.saveDeviceDetails(userAgent, user);
                return  authenticateUser(authentication, response, loginDTO);
            } else {
                log.warn("Invalid TFA code for user: {}", loginDTO.getEmail());
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Invalid Verification Code"));
            }
        } catch (DisabledException e) {
            log.error("User email not verified: {}", loginDTO.getEmail(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response("Please verify your email"));
        } catch (LockedException e) {
            log.error("User account is banned: {}", loginDTO.getEmail(), e);
            return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response("Your account has been banned"));
        } catch (AuthenticationException e) {
            log.error("Authentication failed for user: {}", loginDTO.getEmail(), e);
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Incorrect email or password"));
        }
    }

    private ResponseEntity<?> authenticateUser(Authentication authentication, @NonNull HttpServletResponse response, LoginDTO loginDTO) {
        log.info("Starting Authentication...");
        log.info("Email :" + loginDTO.getEmail());
        SecurityContextHolder.getContext().setAuthentication(authentication);

        String accessToken = jwtUtils.generateJwtToken(authentication.getName());
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(accessToken, jwtExpirationMs).toString());

        User userDetails = (User) authentication.getPrincipal();
        RefreshToken refreshToken = null;
        userDetails.getActivity().setLastLogin(Instant.now());
        userDetails.getActivity().setLoginCount(userDetails.getActivity().getLoginCount() + 1);
        if (loginDTO.isRememberMe()) {
            refreshToken = iRefreshTokenService.createRefreshToken(loginDTO.getEmail(), refreshRememberMeExpirationMs);
            userDetails.getSecurity().setRememberMe(true);
            log.info("RememberMe : On");
        } else {
            refreshToken = iRefreshTokenService.createRefreshToken(loginDTO.getEmail(), refreshExpirationMs);
            userDetails.getSecurity().setRememberMe(false);
            log.info("RememberMe : Off");
        }

        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie(refreshToken.getToken(), loginDTO.isRememberMe() ? refreshRememberMeExpirationMs : refreshExpirationMs).toString());

        userRepository.save(userDetails);
        return ResponseEntity.ok().build();
    }


    public ResponseEntity<?> confirmDevice(String userAgent, @NonNull HttpServletResponse response, LoginDTO loginDTO, Integer code) {
        log.info("Started Confirming Device...");
        User user = userRepository.findUserByEmail(loginDTO.getEmail());
        VerificationToken verificationToken = verificationTokenRepository.findByToken(String.valueOf(code));
        if (verificationToken == null) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Wrong Verification Code"));
        }
        if (Objects.equals(verificationToken.getUser().getId(), user.getId())) {
            try {
                Authentication authentication = authenticationManager.authenticate(
                        new UsernamePasswordAuthenticationToken(loginDTO.getEmail(), loginDTO.getPassword()));
                ResponseEntity<?> response1 = authenticateUser(authentication, response, loginDTO);
                User checkUser = userRepository.findUserByEmail(loginDTO.getEmail());
                iDeviceMetadataService.saveDeviceDetails(userAgent, checkUser);
                log.info("Finished Confirming Device...");
                return response1;
            } catch (DisabledException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response("Please verify your email"));
            } catch (LockedException e) {
                return ResponseEntity.status(HttpStatus.FORBIDDEN).body(new Response("Your account has been banned"));
            } catch (AuthenticationException e) {
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Incorrect email or password"));
            }
        }
        log.info("Started Confirming Device...");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(new Response("Wrong Verification Code"));
    }
    public ResponseEntity<?> generateTwoFactorAuthQrCode(String email) {
        User user = userRepository.findUserByEmail(email);
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        final GoogleAuthenticatorKey key = gAuth.createCredentials();

        user.getSecurity().setTwoFactorAuthKey(key.getKey());
        userRepository.save(user);

        String qrCodeData = "otpauth://totp/" + email + "?secret=" + key.getKey() + "&issuer=Courzelo";
        byte[] qrCodeImage;
        try {
            qrCodeImage = generateQRCodeImage(qrCodeData, 200, 200);
        } catch (WriterException | IOException e) {
            throw new RuntimeException("Could not generate QR code", e);
        }
        String qrCodeImageBase64 = Base64.getEncoder().encodeToString(qrCodeImage);
        return ResponseEntity.ok().body(new QRCodeResponse(qrCodeImageBase64));
    }
    public ResponseEntity<?> enableTwoFactorAuth(String email,String verificationCode){
        User user = userRepository.findUserByEmail(email);
        if (verifyTwoFactorAuth(email, Integer.parseInt(verificationCode))) {
            user.getSecurity().setTwoFactorAuthEnabled(true);
            user.getActivity().setUpdatedAt(Instant.now());
            userRepository.save(user);
            return ResponseEntity.ok().body(new Response("Two Factor Authentication Enabled"));
        }
        return ResponseEntity.badRequest().body(new Response("Invalid Verification Code"));
    }
    public void disableTwoFactorAuth(String email){
        User user = userRepository.findUserByEmail(email);
        user.getSecurity().setTwoFactorAuthKey(null);
        user.getSecurity().setTwoFactorAuthEnabled(false);
        user.getActivity().setUpdatedAt(Instant.now());
        userRepository.save(user);
    }
    public boolean verifyTwoFactorAuth(String email, int verificationCode) {
        log.info("Starting TFA verification for user: {}", email);
        User user = userRepository.findUserByEmail(email);
        if(user == null) {
            log.warn("User not found: {}", email);
            return false;
        }
        GoogleAuthenticator gAuth = new GoogleAuthenticator();
        boolean isCodeValid = gAuth.authorize(user.getSecurity().getTwoFactorAuthKey(), verificationCode);
        if(isCodeValid) {
            log.info("TFA code verified for user: {}", email);
        } else {
            log.warn("Invalid TFA code {} for user: {}", verificationCode ,email);
        }
        return isCodeValid;
    }
    public byte[] generateQRCodeImage(String text, int width, int height) throws WriterException, IOException {
        QRCodeWriter qrCodeWriter = new QRCodeWriter();
        Map<EncodeHintType, ErrorCorrectionLevel> hints = new HashMap<>();
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);
        BitMatrix bitMatrix = qrCodeWriter.encode(text, BarcodeFormat.QR_CODE, width, height, hints);

        ByteArrayOutputStream pngOutputStream = new ByteArrayOutputStream();
        MatrixToImageWriter.writeToStream(bitMatrix, "PNG", pngOutputStream);
        return pngOutputStream.toByteArray();
    }

    public ResponseEntity<Response> verifyAccount(String code) {
        log.info("Started Verifying...");
        log.info(code);
        VerificationToken verificationToken = verificationTokenRepository.findByToken(code);
        if (verificationToken == null) {
            throw new PasswordResetTokenNotFoundException("PasswordResetToken Not Found " + code);
        }
        if (!verificationToken.getVerificationTokenType().equals(VerificationTokenType.EMAIL_VERIFICATION)) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response("Token not valid"));
        }
        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            throw new PasswordResetTokenExpiredException("PasswordResetToken Expired " + verificationToken.getExpiryDate());
        }
        User user = userRepository.findUserById(verificationToken.getUser().getId());
        if (user != null) {
            log.info(user.getEmail());
            user.getSecurity().setEnabled(true);
            user.getActivity().setUpdatedAt(Instant.now());
            userRepository.save(user);
            log.info("Finished Verifying...");
            return ResponseEntity.status(HttpStatus.OK).body(new Response("Account Verified"));
        }
        log.info("Finished Verifying...");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(new Response("Verification Failed"));
    }

    @Override
    public ResponseEntity<Boolean> isAuthenticated(Principal principal) {
      /*  if (principal != null) {
            log.info("User authenticated"); */
            return ResponseEntity.ok().body(true);
       /* }
        log.info("User not authenticated");
        return ResponseEntity.ok().body(false); */
    }

    @PreAuthorize("isAuthenticated()")
    @Override
    public ResponseEntity<List<Role>> getRole(Principal principal) {
        User user = userRepository.findUserByEmail(principal.getName());
        if (user != null) {
            return ResponseEntity.ok().body(user.getRoles());
        }
        return null;
    }

    @Override
    public ResponseEntity<Response> forgotPassword(String email) throws MessagingException, UnsupportedEncodingException {
        log.info("Forgot password started....");
        log.info("Finding user...");
        User user = userRepository.findUserByEmail(email);
        if (user == null) {
            throw new UserNotFoundException("User " + email + " not found");
        }
        log.info("User found");
        VerificationToken verificationToken = new VerificationToken(UUID.randomUUID().toString(), user, VerificationTokenType.FORGOT_PASSWORD);
        verificationTokenRepository.save(verificationToken);
        emailService.sendPasswordChangeEmail(user, verificationToken);
        return ResponseEntity
                .ok()
                .body(new Response("Email sent to " + email));
    }

    @Override
    public ResponseEntity<Response> recoverPassword(String token, RecoverPasswordDTO passwordDTO) {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) {
            throw new PasswordResetTokenNotFoundException("PasswordResetToken Not Found " + token);
        }
        if (!verificationToken.getVerificationTokenType().equals(VerificationTokenType.FORGOT_PASSWORD)) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response("Token not valid"));
        }
        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            throw new PasswordResetTokenExpiredException("PasswordResetToken Expired " + verificationToken.getExpiryDate());
        }
        User user = userRepository.findUserById(verificationToken.getUser().getId());
        if (user == null) {
            throw new UserNotFoundException("User Not Found with id " + verificationToken.getUser().getId());
        }
        user.setPassword(encoder.encode(passwordDTO.getPassword()));
        user.getActivity().setUpdatedAt(Instant.now());
        userRepository.save(user);
        verificationTokenRepository.delete(verificationToken);
        return ResponseEntity
                .ok()
                .body(new Response("Password Changed!"));
    }

    public ResponseEntity<Response> saveUser(User user, String userAgent) {
        log.info("Started Signing up...");
        if (Boolean.TRUE.equals(userRepository.existsByEmail(user.getEmail()))) {
            return ResponseEntity
                    .badRequest()
                    .body(new Response("Email is already in use!"));
        }
        user.setPassword(encoder.encode(user.getPassword()));
        user.getRoles().add(Role.STUDENT);
        user.getSecurity().setEnabled(false);
        user.getSecurity().setBan(false);
        user.getSecurity().setTwoFactorAuthEnabled(false);
        user.getActivity().setCreatedAt(Instant.now());
        user.getActivity().setLoginCount(0);
        userRepository.save(user);
        String randomCode = RandomString.make(64);
        VerificationToken verificationToken = new VerificationToken(
                randomCode,
                user,
                VerificationTokenType.EMAIL_VERIFICATION
        );
        verificationTokenRepository.save(verificationToken);
        iDeviceMetadataService.saveDeviceDetails(userAgent, user);
        try {
            emailService.sendVerificationEmail(user, verificationToken);
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
        log.info("Finished Signing up...");
        return ResponseEntity
                .ok()
                .body(new Response("Account Created!"));
    }

    public void logout(@NonNull HttpServletResponse response ,Principal principal) {
        User user = userRepository.findUserByEmail(principal.getName());
        log.info("Logout :Logging out...");
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie("accessToken", 0L).toString());
        log.info("Logout: Access Token removed");
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createRefreshTokenCookie("refreshToken", 0L).toString());
        log.info("Logout :Refresh Token removed");
        SecurityContextHolder.clearContext();
        log.info("Logout :Security context cleared!");
        user.getActivity().setLastLogout(Instant.now());
        userRepository.save(user);
        log.info("Logout :Logout Finished!");
    }

    public void refreshToken(@NonNull HttpServletResponse response, String email) {
        log.info("refreshToken :Refreshing Token...");
        response.addHeader(HttpHeaders.SET_COOKIE, cookieUtil.createAccessTokenCookie(jwtUtils.generateJwtToken(email), jwtExpirationMs).toString());
        log.info("refreshToken :Access token created!");
        log.info("refreshToken :Refreshing token DONE!");
    }

}
