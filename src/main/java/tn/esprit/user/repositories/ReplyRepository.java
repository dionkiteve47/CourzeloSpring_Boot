package tn.esprit.user.repositories;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Repository;
import tn.esprit.user.UserApplication;
import tn.esprit.user.entities.Reply;

@Repository
public interface ReplyRepository extends MongoRepository<Reply,String> {

}
