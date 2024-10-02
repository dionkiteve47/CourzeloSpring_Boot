package tn.esprit.user.entities.institution;


import tn.esprit.user.entities.User;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextScore;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;


@Document(collection = "institutions")
@Data
public class Institution {
    @Id
    private String id;
    @TextIndexed(weight = 1)
    private String name;
    private String location;
    @TextIndexed(weight = 1)
    private String description;
    private String website;
    @DBRef
    private List<User> admins = new ArrayList<>();
    @DBRef
    private List<User> teachers = new ArrayList<>();
    @DBRef
    private List<User> students = new ArrayList<>();
    @DBRef
    private List<Program> programs = new ArrayList<>();
    private byte[] excelFile;
    private double latitude;
    private double longitude;
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Institution institution = (Institution) o;
        return Objects.equals(id, institution.id);
    }

}
