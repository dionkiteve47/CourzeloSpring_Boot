package tn.esprit.user.services.Implementations;


import tn.esprit.user.entities.Commentaire;
import tn.esprit.user.entities.Post;
import tn.esprit.user.repositories.CommentaireRepository;
import tn.esprit.user.services.Interfaces.ICommentaireService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class CommentaireService implements ICommentaireService {
    @Autowired
    private  final CommentaireRepository commentaireRepository;
    @Autowired
    private PostService postService;
    private static final String[] BAD_WORDS = {"fuck", "pute","kys"}; // Ajoutez vos mots interdits ici

    @Override
    public void saveCommentaire(Commentaire commentaire, String idPost) {
        List<Commentaire> comments ;
        String filtered = commentaire.getComment();
        for (String word : BAD_WORDS) {
            // Remplace toutes les occurrences du mot interdit par des astérisques
            filtered = filtered.replaceAll("(?i)" + word, "*".repeat(word.length()));
        }
        commentaire.setComment(filtered);
        Commentaire c =commentaireRepository.save(commentaire);
        Post p =postService.getPostByID(idPost);
        if (p.getComments() == null) {
            comments = new ArrayList<>();
        }
        else{
            comments = p.getComments();
        }
        comments.add(c);
        p.setComments(comments);


        postService.savePost(p);

    }


    @Override
    public void deleteCommentaire(String commentaireID,String idPost) {
        Post p =postService.getPostByID(idPost);
        p.getComments().remove(getCommentaireByID(commentaireID));
        p.setComments(p.getComments());
        postService.savePost(p);
        commentaireRepository.deleteById(commentaireID);
    }

    @Override
    public void updateCommentaire(Commentaire commentaire) {
        commentaireRepository.save(commentaire);
    }

    @Override
    public Commentaire getCommentaireByID(String commentaireID) {
        return commentaireRepository.findById(commentaireID).orElseThrow(()-> new RuntimeException("Commentaire Not Found!"));
    }

    @Override
    public List<Commentaire> getCommentaires() {
        return commentaireRepository.findAll();
    }
}
