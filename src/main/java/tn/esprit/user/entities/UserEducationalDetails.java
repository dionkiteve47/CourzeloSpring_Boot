package tn.esprit.user.entities;

import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.institution.Institution;
import tn.esprit.user.entities.institution.Program;
import tn.esprit.user.entities.schedule.ElementModule;
import tn.esprit.user.entities.schedule.NonDisponibility;
import lombok.Data;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;

import java.util.ArrayList;
import java.util.List;

@Data
public class UserEducationalDetails {
    @DBRef
    private Institution institution;
    @DBRef
    private Class stclass;
    @DBRef
    private List<Program> programs = new ArrayList<>();
    @DBRef
    private List<ElementModule>elementModules;
    @DBRef
    private List<NonDisponibility>nonDisponibilities;
    public List<Program> getProgram() {
        return programs;
    }
}
