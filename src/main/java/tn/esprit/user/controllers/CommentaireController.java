package tn.esprit.user.controllers;


import tn.esprit.user.entities.Commentaire;
import tn.esprit.user.services.Implementations.CommentaireService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/commentaire")
@CrossOrigin(origins = "http://localhost:4200/", maxAge = 3600, allowedHeaders = "*", allowCredentials = "true")
public class CommentaireController {

    @Autowired
    CommentaireService commentaireService;


    @PostMapping({"/{idPost}"})
    public void saveCommentaire(@RequestBody Commentaire commentaire, @PathVariable String idPost) throws IOException {
        commentaireService.saveCommentaire(commentaire,idPost);
    }
    @PutMapping()
    public void updateCommentaire(@RequestBody Commentaire commentaire){
        commentaireService.updateCommentaire(commentaire);
    }
    @GetMapping("/{id}")
    public Commentaire getCommentaireByID(@PathVariable String id){
        return commentaireService.getCommentaireByID(id);
    }
    @GetMapping()
    public List<Commentaire> getCommentaires(){
        return  commentaireService.getCommentaires();
    }
    @DeleteMapping("/{id}/{idPost}")
    public String  DeleteCommentaires(@PathVariable String id,@PathVariable String idPost) throws IOException {
        commentaireService.deleteCommentaire(id,idPost);
        return  "delete";
    }
}
