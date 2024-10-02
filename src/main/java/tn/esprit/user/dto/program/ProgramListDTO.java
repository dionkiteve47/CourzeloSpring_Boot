package tn.esprit.user.dto.program;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;

@Data
@AllArgsConstructor
public class ProgramListDTO {
    List<ProgramDTO> programs;
    int totalPages;
}
