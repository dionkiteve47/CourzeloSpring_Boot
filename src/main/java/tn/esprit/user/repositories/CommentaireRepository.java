package tn.esprit.user.repositories;

import tn.esprit.user.entities.Commentaire;
import tn.esprit.user.entities.Grades;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CommentaireRepository extends MongoRepository<Commentaire,String>
{
    List<Commentaire> findByIdPost(String idPost);
}
