package tn.esprit.user.repositories;


import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.schedule.ElementModule;
import tn.esprit.user.entities.schedule.Period;
import tn.esprit.user.entities.User;
import jakarta.validation.constraints.NotNull;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.time.DayOfWeek;
import java.util.List;


public interface ElementModuleRepository extends MongoRepository<ElementModule, String> {

    //   ElementModule findByDayOfWeekAndPeriode(DayOfWeek dayOfWeek, Period period);

    //List<ElementModule> findByClasse(String classe);
    // Assuming ElementModuleRepository extends MongoRepository<ElementModule, String>

    // Within your ElementModuleRepository interface
    @Query("{ 'classe': ?0 }")
     List<ElementModule> getElementModulesByClasses(String classeId);
    @Query("{ 'dayOfWeek': ?0, 'period': ?1 }")
    ElementModule findByDayOfWeekAndPeriod(DayOfWeek dayOfWeek, Period period);
    ElementModule findByDayOfWeekAndPeriodAndClasses(@NotNull DayOfWeek dayOfWeek, @NotNull Period period, @NotNull List<Class> classes);
   List<ElementModule> findByDayOfWeekAndPeriodAndTeacher(@NotNull DayOfWeek dayOfWeek, @NotNull Period period, @NotNull User teacher);

    List<ElementModule> findByDayOfWeekAndPeriodAndClasses(DayOfWeek day, Period p, String classeId);
}
