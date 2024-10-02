package tn.esprit.user.repositories;

import tn.esprit.user.entities.Search;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
@Repository
public interface SearchRepository extends MongoRepository<Search, String> {

    @Query(value="{ 'query' : { $regex: ?0 } }", sort="{ 'count' : -1 }")
    List<Search> findTop10ByQueryRegex(String regex, Pageable pageable);

    Search findByQuery(String query);
}
