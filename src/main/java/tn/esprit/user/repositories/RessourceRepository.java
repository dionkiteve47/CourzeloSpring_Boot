package tn.esprit.user.repositories;
import tn.esprit.user.entities.Ressource;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RessourceRepository extends MongoRepository<Ressource,String> {
}