package tn.esprit.user.dto.program;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class InstitutionDTO {
    String id;
    String name;
    String location;
    String description;
    String website;
    double latitude;
    double longitude;
}
