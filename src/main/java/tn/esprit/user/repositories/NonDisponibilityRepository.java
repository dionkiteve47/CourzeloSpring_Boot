package tn.esprit.user.repositories;


import tn.esprit.user.entities.schedule.NonDisponibility;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface NonDisponibilityRepository extends MongoRepository<NonDisponibility, String> {
}
