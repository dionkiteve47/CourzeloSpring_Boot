package tn.esprit.user.services.Implementations;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.*;
import org.springframework.web.client.RestTemplate;
import tn.esprit.user.dtos.*;
import tn.esprit.user.entities.*;
import tn.esprit.user.exceptions.*;
import tn.esprit.user.repositories.*;
import tn.esprit.user.security.Response;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import tn.esprit.user.services.Interfaces.IAuthService;
import tn.esprit.user.services.Interfaces.IPhotoService;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.Principal;
import java.time.Instant;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.Random;
import java.util.stream.Collectors;

import static tn.esprit.user.entities.Role.TEACHER;

@Service
@Slf4j
public class UserService implements UserDetailsService {
    public static final String USER_NOT_FOUND = "User not found with id : ";
    private final UserRepository userRepository;
    private final PasswordEncoder encoder;
    private final EmailService emailService;
    private final IPhotoService iPhotoService;
    private final IAuthService iAuthService;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ClassRepository classRepository;
    private final InstitutionRepository institutionRepository;
    private final MongoTemplate mongoTemplate;
    private final SearchRepository searchRepository;

    private final DeviceMetadataRepository deviceMetadataRepository;
    private final RestTemplate restTemplate;




    public UserService(UserRepository userRepository, @Lazy PasswordEncoder encoder, EmailService emailService, IPhotoService iPhotoService, @Lazy IAuthService iAuthService, VerificationTokenRepository verificationTokenRepository, ClassRepository classRepository, InstitutionRepository institutionRepository, MongoTemplate mongoTemplate, SearchRepository searchRepository, DeviceMetadataRepository deviceMetadataRepository, RestTemplate restTemplate) {
        this.userRepository = userRepository;
        this.encoder = encoder;
        this.emailService = emailService;
        this.iPhotoService = iPhotoService;
        this.iAuthService = iAuthService;
        this.verificationTokenRepository = verificationTokenRepository;
        this.classRepository = classRepository;
        this.institutionRepository = institutionRepository;
        this.mongoTemplate = mongoTemplate;
        this.searchRepository = searchRepository;
        this.deviceMetadataRepository = deviceMetadataRepository;
        this.restTemplate = restTemplate;
    }

    public List<User> searchByKeyword(String keyword ,int page) {
        return userRepository.searchByKeyword(keyword,page, mongoTemplate);
    }
    @Override
    public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(email);
    }

    public UserDetails loadUserByEmail(String email) throws UsernameNotFoundException {
        return userRepository.findUserByEmail(email);
    }

    public ResponseEntity<Response> deleteUser(User user) {
        log.info("deleteUser :Deleting user " + user.getEmail() + "....");
        if (!userRepository.existsById(user.getId())) {
            throw new UserNotFoundException(USER_NOT_FOUND + user.getId());
        }
        userRepository.delete(user);
        log.info("deleteUser :User Deleted!");
        return ResponseEntity.ok().body(new Response("Account Deleted!"));
    }

    public ResponseEntity<Response> updateUserProfile(ProfileDTO profileDTO, String email) {
        log.info("updateUserProfile :Updating user " + email + " profile...");
        User user = userRepository.findUserByEmail(email);
        if (profileDTO.getName() != null && !profileDTO.getName().isEmpty()) {
            log.info("updateUserProfile :Setting name to " + profileDTO.getName());
            user.getProfile().setName(profileDTO.getName());
            log.info("updateUserProfile :Name set to " + user.getProfile().getName());
        }
        if (profileDTO.getLastName() != null && !profileDTO.getLastName().isEmpty()) {
            log.info("updateUserProfile :Setting lastname to " + profileDTO.getLastName());
            user.getProfile().setLastName(profileDTO.getLastName());
            log.info("updateUserProfile :Lastname set to " + user.getProfile().getLastName());
        }
        if(profileDTO.getTitle() != null && !profileDTO.getTitle().isEmpty()){
            log.info("updateUserProfile :Setting title to " + profileDTO.getTitle());
            user.getProfile().setTitle(profileDTO.getTitle());
            log.info("updateUserProfile :Title set to " + user.getProfile().getTitle());
        }
        if(profileDTO.getBio() != null && !profileDTO.getBio().isEmpty()){
            log.info("updateUserProfile :Setting bio to " + profileDTO.getBio());
            user.getProfile().setBio(profileDTO.getBio());
            log.info("updateUserProfile :Bio set to " + user.getProfile().getBio());
        }
        if(profileDTO.getBirthDate() != null && !profileDTO.getBirthDate().toString().isEmpty()){
            log.info("updateUserProfile :Setting birthdate to " + profileDTO.getBirthDate());
            user.getProfile().setBirthDate(profileDTO.getBirthDate());
            log.info("updateUserProfile :Birthdate set to " + user.getProfile().getBirthDate());
        }
        user.getActivity().setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("updateUserProfile :Profile Updated!");
        return ResponseEntity.ok().body(new Response("Profile Updated!"));
    }

    public User getUserByID(String userID) {
        return userRepository.findById(userID)
                .orElseThrow(() -> new UserNotFoundException(USER_NOT_FOUND + userID));
    }

    public UserDTO getMyInfo(String email) {
        User user = userRepository.findUserByEmail(email);
        List<String> roles = user.getAuthorities().stream().map(GrantedAuthority::getAuthority).toList();

        return new UserDTO(
                user.getId(),
                user.getEmail(),
                roles,
                user.getSecurity(),
                user.getProfile(),
                user.getEducation(),
                user.getContact(),
                user.getActivity(),
                user.getSettings(),
                user.getScore()
        );
    }
    public UserContactDTO getMyContactInfo(String email){
        User user = userRepository.findUserByEmail(email);
        return new UserContactDTO(
                user.getContact().getAddress(),
                user.getContact().getPhoneNumber(),
                user.getContact().getWebsite(),
                user.getContact().getLinkedin(),
                user.getContact().getFacebook(),
                user.getContact().getGithub()
        );
    }

    public ResponseEntity<Response> changePassword(PasswordDTO passwordDTO, String email) {
        log.info("changePassword :Changing user " + email + " password...");
        log.info("changePassword :Given Password :" + encoder.encode(passwordDTO.getPassword()));
        User user = userRepository.findUserByEmail(email);
        log.info("changePassword :Actual Password :" + user.getPassword());
        if (!encoder.matches(passwordDTO.getPassword(), user.getPassword())) {
            return ResponseEntity.badRequest().body(new Response("Password is wrong!"));
        }
        log.info("changePassword :Setting password to " + passwordDTO.getNewPassword());
        user.setPassword(encoder.encode(passwordDTO.getNewPassword()));
        log.info("changePassword :Encoded password set to " + user.getPassword());
        user.getActivity().setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("changePassword :Password Changed!");
        return ResponseEntity.ok().body(new Response("Password updated!"));
    }

    public boolean ValidUser(String email) {
        User user = userRepository.findUserByEmail(email);
        return !user.getSecurity().getBan() && user.isEnabled();
    }


    public ResponseEntity<HttpStatus> sendVerificationCode(Principal principal) {
        User user = userRepository.findUserByEmail(principal.getName());
        try {
            Random random = new Random();
            int verificationCode = random.nextInt(9000) + 1000;
            VerificationToken verificationToken = new VerificationToken(
                    String.valueOf(verificationCode),
                    user,
                    VerificationTokenType.UPDATE_EMAIL
            );
            verificationTokenRepository.save(verificationToken);
            emailService.sendVerificationCode(user, verificationToken);
            return ResponseEntity.ok().build();
        } catch (MessagingException | UnsupportedEncodingException e) {
            throw new RuntimeException(e);
        }
    }

    public ResponseEntity<HttpStatus> updateEmail(UpdateEmailDTO updateEmailDTO, Principal principal) {
        User user = userRepository.findUserByEmail(principal.getName());
        VerificationToken verificationToken = verificationTokenRepository.findByToken(String.valueOf(updateEmailDTO.getCode()));
        if (verificationToken == null) {
            throw new PasswordResetTokenNotFoundException("PasswordResetToken Not Found " + updateEmailDTO.getCode());
        }
        if (!verificationToken.getVerificationTokenType().equals(VerificationTokenType.UPDATE_EMAIL)) {
            return ResponseEntity.badRequest().build();
        }
        if (verificationToken.getExpiryDate().isBefore(Instant.now())) {
            throw new PasswordResetTokenExpiredException("PasswordResetToken Expired " + verificationToken.getExpiryDate());
        }
        if (Objects.equals(user.getId(), verificationToken.getUser().getId())) {
            user.setEmail(updateEmailDTO.getEmail());
            user.getActivity().setUpdatedAt(Instant.now());
            userRepository.save(user);
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.badRequest().build();
    }

    public ResponseEntity<HttpStatus> updatePhoto(MultipartFile file, Principal principal) throws IOException {
        User user = userRepository.findUserByEmail(principal.getName());
        user.getProfile().setPhoto(iPhotoService.addPhoto(file));
        user.getActivity().setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("finish update photo");
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<HttpStatus> deleteAccount(DeleteAccountDTO dto, Principal principal, HttpServletRequest request, HttpServletResponse response) {
        User user = userRepository.findUserByEmail(principal.getName());
        if (user != null && dto.getPassword() != null) {
            if (encoder.matches(dto.getPassword(), user.getPassword())) {
                deleteUser(user);
                iAuthService.logout(response,principal);
                return ResponseEntity.ok().build();
            }
            return ResponseEntity.badRequest().build();

        }
        return ResponseEntity.badRequest().build();
    }

    public User getProfById(String id) {
        User user = userRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Teacher with id " + id + " doesn't exist!"));
        // Check if the user has the TEACHER role
        boolean hasTeacherRole = user.getRoles().stream()
                .anyMatch(role -> role == Role.TEACHER);
        if (!hasTeacherRole) {
            throw new RuntimeException("No TEACHER role found for user with id: " + id);
        }
        return user;
    }
    public List<Role> getUserRoles(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User with id " + userId + " doesn't exist!"));
        return user.getRoles();
    }
    public List<User> getProfsByRole() {
        return userRepository.findUsersByRoles(Collections.singletonList(Role.TEACHER));
    }

    public List<User> findTeachersByNameAndRole(String id, String name, List<Role> roles) {
        return userRepository.findUsersByIdAndRolesIsAndProfileName(id, roles, name);
    }

    public User findTeacherByNameAndRole(String id, String name, List<Role> roles) {
        return userRepository.findUserByIdAndRolesIsAndProfileName(id, roles, name);}

    public List<User> findTeachersByNameAndRole(String id,String name, Role role) {
        return userRepository.findUsersByIdAndRolesContainsAndProfileName(id,TEACHER, name);
    }
    public User findTeacherByNameAndRole(String id,String name, Role role) {
        return userRepository.findUserByIdAndRolesContainsAndProfileName(id,TEACHER, name);
    }


    public User addTeacher(User user) {
        // Check if the user is a teacher
        if (!user.getRoles().contains(Role.TEACHER)) {
            throw new IllegalArgumentException("User must be a teacher");
        }
        // Check if the password is null
        /*if (user.getPassword() == null || user.getPassword().isEmpty()) {
            throw new IllegalArgumentException("Password cannot be null");
        }
        // Encode the password
        user.setPassword(encoder.encode(user.getPassword()));*/
        // Save the user in the database
        return userRepository.save(user);
    }


    /* public User addTeacher(User teacher) {
         return userRepository.save(teacher);
     }*/
    public List<User> getTeachers1() {
        return userRepository.findByRolesIs(Collections.singletonList(TEACHER))
                .stream()
                .distinct()
                .collect(Collectors.toList());
    }
    public List<User> getTeachers() {
        return userRepository.findByRolesContains(Role.TEACHER);
    }


    public ResponseEntity<HttpStatus> updateUserContact(String name, UserContactDTO userUpdateDTO) {
        log.info("updateUserDetails :Updating details of user " + name + "...");
        String facebook = "https://www.facebook.com/";
        String linkedin = "https://www.linkedin.com/in/";
        String github = "https://www.github.com/";
        User user = userRepository.findUserByEmail(name);
        user.getContact().setAddress(userUpdateDTO.getUserAddress());
        if(userUpdateDTO.getPhoneNumber() != null && !userUpdateDTO.getPhoneNumber().isEmpty()) {
            user.getContact().setPhoneNumber(userUpdateDTO.getPhoneNumber());
        }
        if(userUpdateDTO.getWebsite() != null && !userUpdateDTO.getWebsite().isEmpty()) {
            user.getContact().setWebsite(userUpdateDTO.getWebsite());
        }
        if(userUpdateDTO.getLinkedin() != null && !userUpdateDTO.getLinkedin().isEmpty()) {
            user.getContact().setLinkedin(linkedin + userUpdateDTO.getLinkedin());
        }
        if(userUpdateDTO.getFacebook() != null && !userUpdateDTO.getFacebook().isEmpty()) {
            user.getContact().setFacebook(facebook + userUpdateDTO.getFacebook());
        }
        if (userUpdateDTO.getGithub() != null && !userUpdateDTO.getGithub().isEmpty()) {
            user.getContact().setGithub(github + userUpdateDTO.getGithub());
        }
        user.getActivity().setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("updateUserDetails :User details Updated!");
        return ResponseEntity.ok().build();
    }

    public ResponseEntity<Response> updateShowBirthDate(String name) {
        log.info("updateShowBirthDate :Updating showBirthDate of user " + name + "...");
        User user = userRepository.findUserByEmail(name);
        user.getSettings().setShowBirthDate(!user.getSettings().isShowBirthDate());
        user.getActivity().setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("updateShowBirthDate :ShowBirthDate Updated!");
        return ResponseEntity.ok().body(new Response("ShowBirthDate Updated!"));
    }

    public ResponseEntity<Response> updateShowAddress(String name) {
        log.info("updateShowAddress :Updating showAddress of user " + name + "...");
        User user = userRepository.findUserByEmail(name);
        user.getSettings().setShowAddress(!user.getSettings().isShowAddress());
        user.getActivity().setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("updateShowAddress :ShowAddress Updated!");
        return ResponseEntity.ok().body(new Response("ShowAddress Updated!"));
    }

    public ResponseEntity<Response> updateShowPhone(String name) {
        log.info("updateShowPhone :Updating showPhone of user " + name + "...");
        User user = userRepository.findUserByEmail(name);
        user.getSettings().setShowPhone(!user.getSettings().isShowPhone());
        user.getActivity().setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("updateShowPhone :ShowPhone Updated!");
        return ResponseEntity.ok().body(new Response("ShowPhone Updated!"));
    }

    public ResponseEntity<HttpStatus> saveSearch(String query) {
        log.info("saveSearch :Saving search query..."+query);
        if(query == null || query.isEmpty()) {
            return ResponseEntity.badRequest().build();
        }
        Search existingSearch = searchRepository.findByQuery(query);
        if(existingSearch != null && query.equals(existingSearch.getQuery())) {
            existingSearch.setCount(existingSearch.getCount()+1);
            searchRepository.save(existingSearch);
            return ResponseEntity.ok().build();
        }
        Search search = new Search(query);
        searchRepository.save(search);
        return ResponseEntity.ok().build();
    }

    public List<Search> getSearchSuggestions(String input) {
        if(input == null || input.isEmpty()) {
            return null;
        }
        return searchRepository.findTop10ByQueryRegex(input, PageRequest.of(0, 10));
    }

    public ResponseEntity<Response> updateSkill(String name, String[] skills) {
        log.info("addSkill :Updating skills to user " + name + "...");
        User user = userRepository.findUserByEmail(name);
        user.getProfile().setSkills(List.of(skills));
        user.getActivity().setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("addSkill :Skills added!");
        return ResponseEntity.ok().body(new Response("Skills added!"));
    }

    public ResponseEntity<Response> removeSkill(String name, String[] skills) {
        log.info("removeSkill :Removing skills from user " + name + "...");
        User user = userRepository.findUserByEmail(name);
        if(user.getProfile().getSkills()==null){
            user.getProfile().setSkills(Collections.emptyList());
        }
        for (String skill : skills) {
            if(user.getProfile().getSkills().contains(skill)){
                user.getProfile().getSkills().remove(skill);
            }else {
                log.info("removeSkill :Skill doesn't exist!");
            }
        }
        user.getActivity().setUpdatedAt(Instant.now());
        userRepository.save(user);
        log.info("removeSkill :Skills removed!");
        return ResponseEntity.ok().body(new Response("Skills removed!"));
    }



    public ResponseEntity<Boolean> predictTFA(String name) throws JsonProcessingException {
        User user = userRepository.findUserByEmail(name);
        long deviceCount = deviceMetadataRepository.countByUser(user);
        // Create headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Create request with user device count and user last login and last logout
        String[] input = new String[]{String.valueOf(deviceCount), String.valueOf(user.getActivity().getLastLogin()), String.valueOf(user.getActivity().getLastLogout())};
        log.info("predictTFA :Input :"+new ObjectMapper().writeValueAsString(input));
        HttpEntity<String[]> request = new HttpEntity<>(input, headers);

        // Make a POST request
        ResponseEntity<Integer> tfaResponse = restTemplate.exchange(
                "http://localhost:5000/predictTFA",
                HttpMethod.POST,
                request,
                Integer.class
        );
        int tfaBool = Integer.parseInt(String.valueOf(tfaResponse.getBody()));
        return ResponseEntity.ok(tfaBool == 1);
    }





}
