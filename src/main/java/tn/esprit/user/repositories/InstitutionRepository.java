package tn.esprit.user.repositories;

import tn.esprit.user.entities.institution.Institution;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface InstitutionRepository extends MongoRepository<Institution, String> {
}
