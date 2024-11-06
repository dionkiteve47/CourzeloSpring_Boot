package tn.esprit.user;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.ResponseEntity;
import tn.esprit.user.dtos.ProfileDTO;
import tn.esprit.user.entities.User;
import tn.esprit.user.exceptions.UserNotFoundException;
import tn.esprit.user.repositories.UserRepository;
import tn.esprit.user.services.Implementations.UserService;
import tn.esprit.user.security.Response;
import java.util.Collections;
import java.util.Optional;
import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplMockTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    private User testUser;

    @BeforeEach
    void setUp() {
        testUser = new User("test@example.com", "password", "FirstName", "LastName");
        testUser.setRoles(Collections.emptyList()); // Assuming roles are optional for this test
    }

    @Test
    void testGetUserByID_UserExists() {
        when(userRepository.findById("123")).thenReturn(Optional.of(testUser));

        User result = userService.getUserByID("123");

        assertEquals(testUser, result);
        verify(userRepository, times(1)).findById("123");
    }

    @Test
    void testGetUserByID_UserNotFound() {
        when(userRepository.findById("nonexistent")).thenReturn(Optional.empty());

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.getUserByID("nonexistent");
        });

        assertEquals("User not found with id : nonexistent", exception.getMessage());
        verify(userRepository, times(1)).findById("nonexistent");
    }

    @Test
    void testUpdateUserProfile() {
        ProfileDTO profileDTO = new ProfileDTO();
        profileDTO.setName("NewName");
        profileDTO.setLastName("NewLastName");

        when(userRepository.findUserByEmail(testUser.getEmail())).thenReturn(testUser);
        when(userRepository.save(any(User.class))).thenReturn(testUser);

        ResponseEntity<Response> response = userService.updateUserProfile(profileDTO, testUser.getEmail());

        assertNotNull(response.getBody());
        assertEquals("Profile Updated!", response.getBody().getMsg()); // Adjusted method name
        assertEquals("NewName", testUser.getProfile().getName());
        assertEquals("NewLastName", testUser.getProfile().getLastName());
        verify(userRepository, times(1)).save(testUser);
    }

    @Test
    void testDeleteUser_UserExists() {
        when(userRepository.existsById(testUser.getId())).thenReturn(true);

        ResponseEntity<Response> response = userService.deleteUser(testUser);

        assertNotNull(response.getBody());
        assertEquals("Account Deleted!", response.getBody().getMsg()); // Adjusted method name
        verify(userRepository, times(1)).delete(testUser);
    }

    @Test
    void testDeleteUser_UserNotFound() {
        when(userRepository.existsById(testUser.getId())).thenReturn(false);

        Exception exception = assertThrows(UserNotFoundException.class, () -> {
            userService.deleteUser(testUser);
        });

        assertEquals("User not found with id : null", exception.getMessage());
        verify(userRepository, never()).delete(testUser);
    }
}
