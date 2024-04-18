package tn.esprit.user.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import tn.esprit.user.entities.Avis;
@Repository
public interface AvisRepository extends MongoRepository<Avis, String> {
}
