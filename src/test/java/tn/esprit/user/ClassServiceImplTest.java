package tn.esprit.user;

import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import tn.esprit.user.dto.program.ClassDTO;
import org.junit.jupiter.api.MethodOrderer.OrderAnnotation;
import tn.esprit.user.services.Interfaces.IClassService;
import org.springframework.http.ResponseEntity;
import java.util.List;

@SpringBootTest
@TestMethodOrder(OrderAnnotation.class)
public class ClassServiceImplTest {
    @Autowired
    IClassService classService;
    @Test
    @Order(1)
    public void testRetrieveAllUsers() {
        // Assuming getClasses() returns a ResponseEntity<List<ClassDTO>>
        ResponseEntity<List<ClassDTO>> response = classService.getClasses();
        // Extract the body (List<ClassDTO>) from ResponseEntity
        List<ClassDTO> listUsers = response.getBody();
        // Ensure listUsers is not null before checking the size
        Assertions.assertNotNull(listUsers, "The list of users should not be null");
        Assertions.assertEquals(3, listUsers.size());
    }
}
