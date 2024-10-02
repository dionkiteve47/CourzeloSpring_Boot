package tn.esprit.user.dto.schedule;

import tn.esprit.user.entities.schedule.NonDisponibility;
import tn.esprit.user.entities.schedule.Period;
import tn.esprit.user.entities.User;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.time.DayOfWeek;

@JsonInclude(JsonInclude.Include.NON_EMPTY)
@Data
public class NonDisponibilityDTO {
    private String  id;
    private DayOfWeek dayOfWeek;
    private Period period;
}
