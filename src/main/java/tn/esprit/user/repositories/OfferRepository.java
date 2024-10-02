package tn.esprit.user.repositories;

import tn.esprit.user.entities.Offer;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.TextCriteria;
import org.springframework.data.mongodb.core.query.TextQuery;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface OfferRepository extends MongoRepository<Offer,String> {

    default List<Offer> searchByKeyword(String keyword, int page, MongoTemplate mongoTemplate) {
        PageRequest pageRequest = PageRequest.of(page, 8);
        Query query = TextQuery.queryText(new TextCriteria().matching(keyword)).sortByScore().with(
                pageRequest
        );
        return mongoTemplate.find(query, Offer.class);
    }
}
