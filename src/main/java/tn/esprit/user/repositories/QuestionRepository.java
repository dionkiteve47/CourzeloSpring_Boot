package tn.esprit.user.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.user.entities.Question;

@Repository
public interface QuestionRepository extends MongoRepository<Question, String> {

}
