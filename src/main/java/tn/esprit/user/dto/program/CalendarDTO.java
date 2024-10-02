package tn.esprit.user.dto.program;

import lombok.Data;

import java.util.Date;

@Data
public class CalendarDTO {
    Date startDate;
    Date finishDate;
    String name;
    String color;
}
