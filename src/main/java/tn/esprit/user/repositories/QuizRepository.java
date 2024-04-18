package tn.esprit.user.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.user.entities.Quiz;

@Repository
public interface QuizRepository extends MongoRepository<Quiz, String> {

}
