package tn.esprit.user.repositories;

//import com.example.springback.Entity.Activity;
//import com.example.springback.Entity.ActivityStats;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;
import org.springframework.data.mongodb.repository.Aggregation;
import tn.esprit.user.entities.Activity;
import tn.esprit.user.entities.ActivityStats;

public interface ActivityRepo extends MongoRepository<Activity,String> {
    List<Activity> findByActivityNameContainingIgnoreCase(String name);
    @Aggregation("{ '$group': { '_id': '$club', 'numberOfActivities': { '$sum': 1 }, 'club': { '$first': '$club' } } }")
    List<ActivityStats> countTotalActivitiesByClub();

}
