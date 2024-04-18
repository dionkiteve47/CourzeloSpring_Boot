package tn.esprit.user.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.user.entities.Quiz;

import java.util.List;

public interface QuizDao extends MongoRepository<Quiz, String> {
    List<Quiz> findByCategory(String category);

}
