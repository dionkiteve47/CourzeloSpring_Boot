package tn.esprit.user.dto.program;

import tn.esprit.user.entities.institution.Institution;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimplifiedInstitutionDTO {
    String id;
    String name;

    public SimplifiedInstitutionDTO(Institution institution) {
        this.id = institution.getId();
        this.name = institution.getName();
    }
}
