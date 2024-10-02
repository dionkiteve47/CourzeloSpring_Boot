package tn.esprit.user.repositories;

import tn.esprit.user.entities.Grades;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface GradeRepository extends MongoRepository<Grades,String>
{
}
