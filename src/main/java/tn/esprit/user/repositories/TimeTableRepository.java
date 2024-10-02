package tn.esprit.user.repositories;

import tn.esprit.user.entities.schedule.TimeTable;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface TimeTableRepository extends MongoRepository<TimeTable, String> {

}
