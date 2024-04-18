package tn.esprit.user.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.user.entities.QuizSubmission;

import java.util.List;

@Repository

public interface QuizSubmissionRepository extends MongoRepository<QuizSubmission, String> {
    List<QuizSubmission> findByQuizId(String quizId);

}
