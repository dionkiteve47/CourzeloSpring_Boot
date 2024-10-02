package tn.esprit.user.repositories;


import tn.esprit.user.entities.schedule.FieldOfStudy;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface FieldOfStudyRepository extends MongoRepository<FieldOfStudy, String> {
}
