package tn.esprit.user.repositories;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Repository;
import tn.esprit.user.entities.Cours;
import java.util.Date;
import java.util.List;

@Repository
@EnableScheduling
@SpringBootApplication
public interface CoursRepository extends MongoRepository<Cours,String> {
    List<Cours> findAllByNomCours(String  nomCours);
    List<Cours> findAllByOrderByDateInscriptionDesc();
    List<Cours> findByDateInscriptionGreaterThan(Date dateInscription);
    List<Cours> findByPrix(float prix);
    List<Cours> findByDescriptionCoursIgnoreCaseOrNomProfesseur(String descriptionCours, String nomProfesseur);
List<Cours> findAllByOrderByPrixAsc();
    List<Cours> findAllByOrderByPrixDesc();

}
