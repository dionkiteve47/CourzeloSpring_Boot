package tn.esprit.user.dto.program;

import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.schedule.FieldOfStudy;
import tn.esprit.user.entities.schedule.Modul;
import tn.esprit.user.entities.schedule.Semester;
import tn.esprit.user.entities.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SimplifiedClassDTO {
    private String id;
    private String name;

    public SimplifiedClassDTO(Class stclass) {
        this.id = stclass.getId();
        this.name = stclass.getName();
    }
}
