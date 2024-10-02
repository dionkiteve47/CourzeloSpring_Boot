package tn.esprit.user.repositories;

import tn.esprit.user.entities.Vote;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Voterepository extends MongoRepository<Vote, String > {

    List<Vote> findByIdComment(String idComment);
    Vote findByIdCommentAndIdUser(String idComment, String user);
    int countByTypeAndIdComment(int type, String idComment);
}
