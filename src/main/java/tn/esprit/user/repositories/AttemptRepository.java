package tn.esprit.user.repositories;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.user.entities.UserAttempt;

@Repository
public interface AttemptRepository extends MongoRepository<UserAttempt, String> {
}
