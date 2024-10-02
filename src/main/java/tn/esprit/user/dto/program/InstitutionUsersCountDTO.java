package tn.esprit.user.dto.program;

import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class InstitutionUsersCountDTO {
    int admins;
    int teachers;
    int students;
}
