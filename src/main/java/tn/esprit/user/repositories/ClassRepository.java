package tn.esprit.user.repositories;

import tn.esprit.user.entities.institution.Class;
import tn.esprit.user.entities.schedule.SemesterNumber;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ClassRepository extends MongoRepository<Class, String> {
   List<Class> findBySemester_SemesterNumber(SemesterNumber semesterNumber);

}
