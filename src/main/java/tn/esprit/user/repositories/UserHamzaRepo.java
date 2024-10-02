package tn.esprit.user.repositories;

//import com.example.springback.Entity.UserHamza;
import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.user.entities.UserHamza;

public interface UserHamzaRepo extends MongoRepository<UserHamza,String> {
}
