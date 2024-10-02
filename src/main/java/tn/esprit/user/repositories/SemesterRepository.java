package tn.esprit.user.repositories;

import tn.esprit.user.entities.schedule.Semester;
import tn.esprit.user.entities.schedule.SemesterNumber;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;


public interface SemesterRepository extends MongoRepository<Semester, String> {
    List<Semester> findSemesterBySemesterNumber(SemesterNumber semesterNumber);
}
