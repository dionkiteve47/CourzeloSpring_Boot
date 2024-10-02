package tn.esprit.user.services.Implementations;

import tn.esprit.user.entities.Vote;
import tn.esprit.user.repositories.Voterepository;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class VoteService {
    
    @Autowired
    private final Voterepository voterepository ;


    
    public void saveVote(Vote vote) {

        Vote v= voterepository.findByIdCommentAndIdUser(vote.getIdComment(),vote.getIdUser());
        if (v == null){
            voterepository.save(vote);
        }
        else{
            voterepository.deleteById(v.getId());
            voterepository.save(vote);
        }


    }

    
    public void deleteVote(String voteID) {
        voterepository.deleteById(voteID);
    }

    
    public void updateVote(Vote vote) {
        voterepository.save(vote);
    }

    
    public Vote getVoteByID(String voteID) {
        return voterepository.findById(voteID).orElseThrow(()-> new RuntimeException("Vote Not Found!"));
    }

    
    public List<Vote> getVotes(String idcomment) {
        return voterepository.findByIdComment(idcomment);
    }

    public int getVoteByTypeAndComment(int type ,String idcomment) {
        return voterepository.countByTypeAndIdComment(type, idcomment);
    }
}
