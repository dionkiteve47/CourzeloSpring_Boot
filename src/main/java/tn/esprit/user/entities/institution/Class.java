package tn.esprit.user.entities.institution;

import tn.esprit.user.entities.schedule.FieldOfStudy;
import tn.esprit.user.entities.schedule.Modul;
import tn.esprit.user.entities.schedule.Semester;
import tn.esprit.user.entities.User;
import jakarta.persistence.Id;
import lombok.Data;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextScore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Document(collection = "classes")
@Data
public class Class {

    @Id
    private String id;
    private String name;
    private Integer capacity;
    @DBRef
    private Program program;
    @DBRef
    private List<User> teachers = new ArrayList<>();
    @DBRef
    private List<User> students = new ArrayList<>();
    @DBRef
    private FieldOfStudy fieldOfStudy;
    @DBRef
    private List<Modul> moduls;
    @DBRef
    private Semester semester;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Class that = (Class) o;
        return Objects.equals(id, that.id) &&
                Objects.equals(name, that.name) &&
                Objects.equals(capacity, that.capacity);
    }
    @TextScore
    private Float score;
}
