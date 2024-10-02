package tn.esprit.user.entities.schedule;

import tn.esprit.user.entities.User;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;


@Document(collection = "NonDisponibility")
@Data
public class NonDisponibility {

    @Id
    private  String id;
    @NotNull
    private DayOfWeek dayOfWeek;
    @NotNull
    private Period period;
    @DBRef
    private User teacher;

}
