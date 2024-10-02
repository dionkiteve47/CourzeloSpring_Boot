package tn.esprit.user.repositories;

import tn.esprit.user.entities.schedule.Modul;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ModulRepository extends MongoRepository<Modul,String> {
    boolean existsByIdIgnoreCase(String id);
}
