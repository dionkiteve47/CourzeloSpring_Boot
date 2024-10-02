package tn.esprit.user.controllers;


import tn.esprit.user.entities.Vote;
import tn.esprit.user.services.Implementations.VoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@RestController
@RequestMapping("/vote")
@CrossOrigin(origins = "*")
public class VoteController {

    @Autowired
    VoteService postService;


    @PostMapping()
    public void saveVote(@RequestBody Vote p){
        postService.saveVote(p);
    }
    @PutMapping()
    public void updateVote(@RequestBody Vote p){
        postService.updateVote(p);
    }
    @GetMapping("/{postId}")
    public Vote getVoteByID(@PathVariable String postId){
        return postService.getVoteByID(postId);
    }
    @GetMapping("/{idComment}")
    public List<Vote> getVote(@PathVariable String idComment){

        return  postService.getVotes(idComment);
    }

    @DeleteMapping("/{id}")
    public String  DeleteVote(@PathVariable String id) {
        postService.deleteVote(id);
        return "delete";
    }

    @GetMapping("/{type}/{idComment}")
    public int getVoteByTypeAndIdComment(@PathVariable int type ,@PathVariable String idComment){

        return  postService.getVoteByTypeAndComment(type,idComment);
    }
}
