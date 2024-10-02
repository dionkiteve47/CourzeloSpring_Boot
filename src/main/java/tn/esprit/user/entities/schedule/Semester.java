package tn.esprit.user.entities.schedule;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDate;


@Document(collection = "Semester")
@Data
public class Semester {

    @Id
    private String id;
    @NotNull

    private LocalDate startDate;
    @NotNull
    private LocalDate endDate;
    @NotNull
    @Size(max = 255)
    private String universityYear;

    @NotNull
    @NotNull
    private SemesterNumber semesterNumber;


}
