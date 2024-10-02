package tn.esprit.user.entities.schedule;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;


@Document(collection = "Departments")
@Data
public class Department {

    @Id
    private String id;

    @Getter
    @Size(max = 255)
    @NotNull
    private String name;
    @NotNull
    private String chefDepartment;
    @DBRef
    private List<FieldOfStudy> fieldOfStudies;


}
