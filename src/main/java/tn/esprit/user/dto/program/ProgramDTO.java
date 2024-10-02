package tn.esprit.user.dto.program;

import lombok.Data;
@Data
public class ProgramDTO {
    String id;
    String name;
    String description;
    String programType;
    String popularity;
    String secretKey;
}