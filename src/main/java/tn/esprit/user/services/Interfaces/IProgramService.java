package tn.esprit.user.services.Interfaces;

import tn.esprit.user.dto.program.ClassDTO;
import tn.esprit.user.dto.program.ClassListDTO;
import tn.esprit.user.dto.program.ProgramDTO;
import tn.esprit.user.dto.program.ProgramListDTO;
import tn.esprit.user.entities.institution.Program;
import com.fasterxml.jackson.core.JsonProcessingException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.security.Principal;
public interface IProgramService {
    ResponseEntity<ProgramListDTO> getPrograms(Principal principal, int page, int sizePerPage);

    ResponseEntity<Boolean> deleteProgram(Principal principal, String programID);

    ResponseEntity<Boolean> addProgram(Principal principal, ProgramDTO programDTO);

    void deleteProgramChain(Program program);

    ResponseEntity<Boolean> updateProgram(Principal principal, ProgramDTO programDTO);

    ResponseEntity<ClassListDTO> getProgramClasses(Principal principal, String programID, int page, int sizePerPage);

    ResponseEntity<Boolean> removeClass(String classID, Principal principal);

    ResponseEntity<Boolean> addClassToProgram(String program, ClassDTO classe, Principal principal);

    ResponseEntity<HttpStatus> joinProgram(String email, String secretKey);

    ResponseEntity<HttpStatus> leaveProgram(String name, String programID);

    ResponseEntity<ProgramListDTO> getMyPrograms(String name);

    ResponseEntity<ProgramDTO> getProgramByClassID(String classID);

    ResponseEntity<ProgramDTO> getProgramSuggestions(String name) throws JsonProcessingException;
    ResponseEntity<HttpStatus> joinProgramByID(String email, String id);
    ResponseEntity<String> predictPopularity(String programID) throws JsonProcessingException;
}