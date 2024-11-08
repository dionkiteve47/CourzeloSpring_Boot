package tn.esprit.user.repositories;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.stereotype.Repository;
import tn.esprit.user.UserApplication;
import tn.esprit.user.entities.Article;
import tn.esprit.user.entities.Post;
import tn.esprit.user.entities.Reply;

import java.util.List;

@Repository
public interface ArticleRepository extends MongoRepository<Article,String> {

    @Query("{ 'followedBy' : ?0 }")
    List<Post> getFollowedArticlesByUserId(String userId);
}
