package tn.esprit.user.repositories;

import org.springframework.data.mongodb.repository.MongoRepository;
import tn.esprit.user.entities.Re_reply;

public interface Re_replyRepository extends MongoRepository<Re_reply,String> {
}
