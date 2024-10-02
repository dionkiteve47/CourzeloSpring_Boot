package tn.esprit.user.repositories;

import tn.esprit.user.entities.institution.Institution;
import tn.esprit.user.entities.institution.Program;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProgramRepository extends MongoRepository<Program, String> {
    Page<Program> findAllByInstitution(Institution institution, Pageable pageable);
    Program findByName(String name);
    List<Program> findByInstitution(Institution institution);
    Program findBySecretKey(String secretKey);


}
