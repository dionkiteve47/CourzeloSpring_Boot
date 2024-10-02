package tn.esprit.user.repositories;

//import com.example.springback.Entity.Club;
import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.user.entities.Club;

public interface ClubRepo extends MongoRepository<Club,String> {
}
