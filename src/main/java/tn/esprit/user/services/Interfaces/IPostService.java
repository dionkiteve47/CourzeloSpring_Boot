package tn.esprit.user.services.Interfaces;

import tn.esprit.user.entities.Post;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

public interface IPostService {


    Post savePost(Post p);
    void deletePost(String p);
    Post updatePost (Post p);
    Post getPostByID(String classID);
    List<Post> getPost();
}
