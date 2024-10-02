package tn.esprit.user.services.Interfaces;

import tn.esprit.user.dto.program.ClassDTO;
import tn.esprit.user.dtos.UserListDTO;
import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.institution.Institution;
import tn.esprit.user.entities.schedule.SemesterNumber;
import tn.esprit.user.entities.User;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface IClassService {
    ResponseEntity<List<ClassDTO>> getClasses();

    ResponseEntity<Boolean> deleteClass(String classID);

    void removeUsersInClass(String classID);

    ResponseEntity<Boolean> addClass(ClassDTO classDTO);
    ResponseEntity<Boolean> addClass1(ClassDTO classDTO);

    ResponseEntity<Boolean> updateClass(ClassDTO classDTO);

    ResponseEntity<UserListDTO> getClassUsers(String classID, Principal principal, String role, int page, int sizePerPage);

    ResponseEntity<Boolean> addUserToClass(String classID, String userEmail, String role);

    ResponseEntity<Boolean> removeUser(String classID, String userEmail);

    boolean userInInstitution(User user, Institution institution);
    List<Class> searchClassesBySemester(SemesterNumber semesterNumber);

    ResponseEntity<List<ClassDTO>> getClassesWithoutPagination();

    ResponseEntity<ClassDTO> getMyClass(Principal principal);
}


