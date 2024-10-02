package tn.esprit.user.services.Implementations;


import tn.esprit.user.entities.Post;

import tn.esprit.user.entities.User;
import tn.esprit.user.repositories.CommentaireRepository;
import tn.esprit.user.repositories.PostRepository;
import tn.esprit.user.repositories.UserRepository;
import tn.esprit.user.services.Interfaces.IPostService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.List;

@Service
public class PostService implements IPostService {

    @Autowired
    PostRepository postRepository ;
    @Autowired
    CommentaireRepository commentaireRepository ;
    @Autowired
    private UserRepository userRepository;

    @Override
    public Post savePost(Post p) {

        return postRepository.save(p);
    }

    public User findUserById(String id) {

        return userRepository.findById(id).get();
    }

    @Override
    public void deletePost(String p) {
        postRepository.deleteById(p);
    }

    @Override
    public Post updatePost(Post p) {
        return postRepository.save(p);
    }

    @Override
    public Post getPostByID(String postId) {

        return postRepository.findById(postId).get();
    }

    @Override
    public List<Post> getPost() {
        System.out.println("hhhh"+postRepository.findAll());
        return postRepository.findAll();
    }

   public void saveImg (MultipartFile imgFile,String id) throws IOException {

        Post p = getPostByID(id);
       if (imgFile != null && !imgFile.isEmpty()) {
           byte[] photoData = imgFile.getBytes();
           System.out.println("Taille des données de la photo : " + photoData);
           p.setImg(photoData);

       }
       savePost(p);

   }

}
