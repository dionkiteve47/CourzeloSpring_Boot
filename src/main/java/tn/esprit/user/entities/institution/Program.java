package tn.esprit.user.entities.institution;

import tn.esprit.user.entities.User;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.time.Instant;

@Document(collection = "programs")
@Data
public class Program {
    @Id
    String id;
    String name;
    String description;
    ProgramType programType;
    Instant lastUpdate;
    String secretKey;
    @DBRef
    Institution institution;
    @DBRef
    List<Class> classes = new ArrayList<>();
    @DBRef
    List<User> students = new ArrayList<>();

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Program program = (Program) o;
        return Objects.equals(id, program.id);
    }

}