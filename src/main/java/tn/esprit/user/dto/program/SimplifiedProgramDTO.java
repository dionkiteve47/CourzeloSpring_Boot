package tn.esprit.user.dto.program;

import tn.esprit.user.entities.institution.Program;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimplifiedProgramDTO {
    String id;
    String name;

    public SimplifiedProgramDTO(Program program) {
        this.id = (program != null) ? program.getId() : null;
        this.name = (program != null) ? program.getName() : null;
    }
    public static List<SimplifiedProgramDTO> fromList(List<Program> programs) {
        return programs.stream().map(SimplifiedProgramDTO::new).toList();
    }
}
