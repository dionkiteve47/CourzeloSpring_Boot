package tn.esprit.user.dtos;

import tn.esprit.user.dto.program.InstitutionDTO;
import tn.esprit.user.dto.program.SimplifiedClassDTO;
import tn.esprit.user.dto.program.SimplifiedInstitutionDTO;
import tn.esprit.user.dto.program.SimplifiedProgramDTO;
import tn.esprit.user.entities.UserEducationalDetails;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserEducationDTO {
    private SimplifiedInstitutionDTO institution;
    private SimplifiedClassDTO stclass;
    private List<SimplifiedProgramDTO> program;

    public UserEducationDTO(UserEducationalDetails education) {
        if (education != null) {
            this.institution = education.getInstitution() != null ? new SimplifiedInstitutionDTO(education.getInstitution()) : null;
            this.stclass = education.getStclass() != null ? new SimplifiedClassDTO(education.getStclass()) : null;
            this.program = education.getProgram() != null ? SimplifiedProgramDTO.fromList(education.getProgram()) : null;
        }
    }
}
