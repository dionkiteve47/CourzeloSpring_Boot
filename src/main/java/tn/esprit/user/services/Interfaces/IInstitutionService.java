package tn.esprit.user.services.Interfaces;


import tn.esprit.user.dto.program.CalendarDTO;
import tn.esprit.user.dto.program.InstitutionDTO;
import tn.esprit.user.dto.program.InstitutionListDTO;
import tn.esprit.user.dto.program.InstitutionUsersCountDTO;
import tn.esprit.user.dtos.UserListDTO;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
import java.util.List;

public interface IInstitutionService {
    ResponseEntity<InstitutionListDTO> getInstitutions(int page, int sizePerPage);

    ResponseEntity<HttpStatus> generateExcel(List<CalendarDTO> events,Principal principal);

    ResponseEntity<InstitutionDTO> getInstitutionByID(String institutionID);

    ResponseEntity<InstitutionDTO> getInstitution(String email);

    ResponseEntity<Boolean> deleteInstitution(String institutionID);

    ResponseEntity<Boolean> addInstitution(InstitutionDTO institutionDTO);

    ResponseEntity<Boolean> updateInstitution(InstitutionDTO institutionDTO);

    ResponseEntity<Boolean> addUserToInstitution(String institutionID, String userEmail, String role, Principal principal);

    ResponseEntity<Boolean> removeUser(String institutionID, String userEmail, Principal principal);

    ResponseEntity<Boolean> updateMyInstitution(InstitutionDTO institutionDTO, Principal principal);

    ResponseEntity<InstitutionUsersCountDTO> countUsers(Principal principal);

    ResponseEntity<UserListDTO> getInstitutionUsers(String institutionID, Principal principal, String role, int page, int sizePerPage);

    ResponseEntity<InstitutionDTO> getMyInstitution(Principal principal);

    ResponseEntity<byte[]> downloadExcel(Principal principal);

    ResponseEntity<Boolean> saveLocation(InstitutionDTO institutionDTO);
}
