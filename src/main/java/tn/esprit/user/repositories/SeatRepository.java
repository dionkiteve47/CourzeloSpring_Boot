package tn.esprit.user.repositories;

//import com.example.springback.Entity.Seat;
import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.user.entities.Seat;

import java.util.List;

public interface SeatRepository extends MongoRepository<Seat, String> {
    List<Seat> findByActivityId(String activityId);
}
