package tn.esprit.user.entities.schedule;

import jakarta.persistence.Id;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.DayOfWeek;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Document(collation = "TimeTables")
@Data

public class TimeTable {
    @Id
    private String id;
    @NotNull
    private String name;
    @DBRef
    private List<Semester> semesters;
    @DBRef
    private List<Department> departments;
    @DBRef
    private List<ElementModule> elementModules;
    private String classe;

    private Map<DayOfWeek, Map<Period, List<ElementModule>>> schedule;

    public TimeTable() {
        schedule = new HashMap<>();
    }

    public void setSchedule(Map<DayOfWeek, Map<Period, List<ElementModule>>> scheduleMap) {
        for (Map.Entry<DayOfWeek, Map<Period, List<ElementModule>>> entry : scheduleMap.entrySet()) {
            DayOfWeek dayOfWeek = entry.getKey();
            Map<Period, List<ElementModule>> daySchedule = entry.getValue();
            for (Map.Entry<Period, List<ElementModule>> periodEntry : daySchedule.entrySet()) {
                Period period = periodEntry.getKey();
                List<ElementModule> modules = periodEntry.getValue();
                setPeriodSchedule(dayOfWeek, period, modules);
            }
        }
    }

    private void setPeriodSchedule(DayOfWeek dayOfWeek, Period period, List<ElementModule> modules) {
        Map<Period, List<ElementModule>> daySchedule = schedule.computeIfAbsent(dayOfWeek, k -> new HashMap<>());
        daySchedule.put(period, modules);
    }

}
