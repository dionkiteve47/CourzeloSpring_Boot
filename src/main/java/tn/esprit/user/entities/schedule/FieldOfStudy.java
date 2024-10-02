package tn.esprit.user.entities.schedule;

import tn.esprit.user.entities.institution.Class;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;
import java.util.Objects;


@Document(collection = "FieldOfStudy")
@Data
public class FieldOfStudy {

    @Id
    private String id;
    @NotNull
    @Size(max = 255)
    private String name;
    @NotNull
    private Integer numbrWeeks;
    @NotNull
    @Size(max = 255)
    private String chefField;
    @DBRef
    private Department department;
    @DBRef
    private Semester semester;
    @DBRef
    private List<Class> classes;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        FieldOfStudy that = (FieldOfStudy) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(numbrWeeks, that.numbrWeeks) &&
                Objects.equals(chefField, that.chefField);
    }

}
