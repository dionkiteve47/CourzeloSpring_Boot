package tn.esprit.user.repositories;

//import com.example.springback.Entity.Reservation;
import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.user.entities.Reservation;

public interface ReservationRepository extends MongoRepository<Reservation, String> {
    // Custom repository methods if needed
}
