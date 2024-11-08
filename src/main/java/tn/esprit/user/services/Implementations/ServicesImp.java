package tn.esprit.user.services.Implementations;

import jakarta.mail.MessagingException;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import tn.esprit.user.controllers.CustomBotController;
import tn.esprit.user.dto.ChatGPTRequest;
import tn.esprit.user.dto.ChatGptResponse;
import tn.esprit.user.entities.Post;
import tn.esprit.user.entities.Re_reply;
import tn.esprit.user.entities.Reply;
import tn.esprit.user.entities.Article;
import tn.esprit.user.repositories.ArticleRepository;
import tn.esprit.user.repositories.PostRepository;
import tn.esprit.user.repositories.Re_replyRepository;
import tn.esprit.user.repositories.ReplyRepository;
import tn.esprit.user.services.Interfaces.IService;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.mail.javamail.JavaMailSender;
import tn.esprit.user.repositories.AttemptRepository;
import tn.esprit.user.repositories.AvisRepository;
import tn.esprit.user.repositories.QuestionRepository;
import tn.esprit.user.repositories.QuizRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;


@Slf4j
@Service
@AllArgsConstructor
public class ServicesImp implements IService {
    private RestTemplate template;
    private static final Logger logger = LoggerFactory.getLogger(CustomBotController.class); // Declare logger
    @Autowired
    private EmailServicee emailService;

    @Autowired
    private ReplyRepository replyRepository;

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private ArticleRepository articleRepository;
    @Autowired
    private Re_replyRepository re_replyRepository;
    private QuizRepository quizRepository;

    private QuestionRepository questionRepository;
    private AvisRepository avisRepository;
    private AttemptRepository attemptRepository;
    private JavaMailSender mailSender;
    @Autowired
    private EmailSenderService emailSenderService;


    public void triggerMail() throws MessagingException {
        emailService.sendSimpleEmail("mohamedaziz.nacib@esprit.tn",
                "Courzelo Platform",
                "Post successfully added!");

    }
    public void followMail() throws MessagingException {
        emailService.sendSimpleEmail("mohamedaziz.nacib@esprit.tn",
                "Courzelo Platform",
                "User x followed your post!");

    }
    public void followedArticle(String article) throws MessagingException {
        emailService.sendSimpleEmail("mohamedaziz.nacib@esprit.tn",
                "Courzelo Platform",
                "A new post was added to your followed article "+article+"!");

    }
    @Override
    public Reply addReply(Reply reply) {
        String response = chat(reply.getContext());
        if (!response.equals("valid")) {
            return null;
        } else {
            // Get the post ID from the reply
            String postId = reply.getPostId();

            // Fetch the associated post
            Post post = postRepository.findById(postId)
                    .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + postId));

            // Initialize the replies list if it's null
            if (post.getReplies() == null) {
                post.setReplies(new ArrayList<>());
            }

            // Add the reply to the post's replies list
            post.getReplies().add(reply);


            // Save the post (which will cascade save the associated reply)
            postRepository.save(post);


            return replyRepository.save(reply);
        }
    }
    @Override
    public Re_reply addRe_reply(Re_reply re_reply) {
        String response = chat(re_reply.getContext());
        if (!response.equals("valid")) {
            return null;
        } else {
            // Get the post ID from the reply
            String replyId = re_reply.getReplyId();

            // Fetch the associated post
            Reply reply = replyRepository.findById(replyId)
                    .orElseThrow(() -> new EntityNotFoundException("Reply not found with id: " + replyId));

            // Initialize the replies list if it's null
            if (reply.getRe_replies() == null) {
                reply.setRe_replies(new ArrayList<>());
            }

            reply.getRe_replies().add(re_reply);


            replyRepository.save(reply);

            return re_replyRepository.save(re_reply);
        }
    }

    @Override
    public Post addPost(Post post) throws MessagingException {
        String response = chat(post.getContext());
        if (!response.equals("valid")) {
            return null;
        } else {
            if (!post.getArticleId().isEmpty()) {

                Optional<Article> articleOptional = articleRepository.findById(post.getArticleId());

                if (articleOptional.isPresent()) {
                    Article article = articleOptional.get();
                    post.setArticle(article);
                    if (checkArticleIsFollowed(post.getArticleId(), "static")) {
                        followedArticle(article.getTitre());
                    }

                } else {
                    return null;
                }
            }

            triggerMail();

            return postRepository.save(post);
        }
    }

public boolean checkArticleIsFollowed(String articleId, String userId){
        Article article = articleRepository.findById(articleId) .orElseThrow(() -> new EntityNotFoundException("article not found with id: " + articleId));
    List<String> followedBy = article.getFollowedBy();
    if (followedBy.contains(userId)) {
        return true;
    }
return false;
}

    @Override
    public Article addArticle(Article article) {
        String response = chat(article.getTitre() +"/n"+article.getCategory());
        if (!response.equals("valid")) {
            return null;
        } else {
        return articleRepository.save(article);
    }}

    @Override
    public List<Article> getAllArticles() {
        return articleRepository.findAll();
    }

    @Override
    public Optional<Article> getArticleById(String id) {
        return articleRepository.findById(id);
    }

    @Override
    public Article updateArticle(String id, Article updatedArticle) {
        String response = chat(updatedArticle.getTitre() +"/n"+updatedArticle.getCategory());
        if (!response.equals("valid")) {
            return null;
        } else {
        Optional<Article> existingArticle = articleRepository.findById(id);

        if (existingArticle.isPresent()) {
            Article originalArticle = existingArticle.get();

            if (updatedArticle.getTitre() != null) {
                originalArticle.setTitre(updatedArticle.getTitre());
            }            if (updatedArticle.getScore() != null) {
                originalArticle.setScore(updatedArticle.getScore());
            }
            if (updatedArticle.getCategory() != null) {
                originalArticle.setCategory(updatedArticle.getCategory());
            }
            if (updatedArticle.getPosts() != null) {
                originalArticle.setPosts(updatedArticle.getPosts());
            }

            // Save the updated article
            return articleRepository.save(originalArticle);
        }

        return null; // Handle the case where the article with the given id doesn't exist
    }}

    @Override
    public boolean deleteArticle(String id) {
        Optional<Article> articleOptional = articleRepository.findById(id);

        if (articleOptional.isPresent()) {
           List<Post> posts = postRepository.findAll();

                posts.forEach(post -> {
                    if (id.equals(post.getArticleId())) {
                        postRepository.deleteById(post.getIdPost());
                    }
                });


            // Delete the article
            articleRepository.deleteById(id);

            return true;
        }

        return false; // Handle the case where the article with the given id doesn't exist
    }





    @Override
    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    @Override
    public Optional<Post> getPostById(String id) {
        return postRepository.findById(id);
    }
    @Override
    public Post updatePost(String id, Post updatedPost) {
        String response = chat(updatedPost.getTitre()+"/n"+updatedPost.getContext());
        if (!response.equals("valid")) {
            return null;
        } else {
        Optional<Post> existingPost = postRepository.findById(id);
        if (!updatedPost.getArticleId().isEmpty()) {

            Optional<Article> articleOptional = articleRepository.findById(updatedPost.getArticleId());

            if (articleOptional.isPresent()) {
                Article article = articleOptional.get();
                existingPost.get().setArticle(article);

            } else {
                return null;
            }
        }

        if (existingPost.isPresent()) {
          //  String reponse = chat(updatedPost.getContext());
          //  if ("valid".equals(reponse)) {
                Post originalPost = existingPost.get();

                // Update only the fields that are not null in the updatedPost
                if (updatedPost.getTitre() != null) {
                    originalPost.setTitre(updatedPost.getTitre());
                }
            if (updatedPost.getContext() != null) {
                originalPost.setContext(updatedPost.getContext());
            }
                if (updatedPost.getImageUrl() != null) {
                    originalPost.setImageUrl(updatedPost.getImageUrl());
                }
                if (updatedPost.getIdUser() != null) {
                    originalPost.setIdUser(updatedPost.getIdUser());
                }
                if (updatedPost.getArticleId() != null) {
                    originalPost.setArticleId(updatedPost.getArticleId());
                }

                if (updatedPost.getReplies() != null) {
                    originalPost.setReplies(updatedPost.getReplies());
                }

                // Save the updated post
                return postRepository.save(originalPost);
           // } else {
           //     return null;
          //  }

        }

        return null; // Handle the case where the post with the given id doesn't exist
    }}

    @Override
    public boolean deletePost(String id) {
        if (postRepository.existsById(id)) {
            postRepository.deleteById(id);
            return true;
        }
        return false; // Handle the case where the post with the given id doesn't exist
    }
    @Override
    public List<Reply> getAllReplies() {
        return replyRepository.findAll();
    }

    @Override
    public List<Re_reply> getAllRe_replies() {
        return re_replyRepository.findAll();
    }

    @Override
    public Optional<Reply> getReplyById(String id) {
        return replyRepository.findById(id);
    }

    @Override
    public Optional<Re_reply> getRe_replyById(String id) {
        return re_replyRepository.findById(id);
    }

    @Override
    public List<Post> getPostsByArticleId(String articleId) {
        return postRepository.getPostsByArticleId(articleId);
    }

    @Override
    public Reply updateReply(String id, Reply updatedReply) {

            Optional<Reply> existingReply = replyRepository.findById(id);

            if (existingReply.isPresent()) {
                String response = chat(updatedReply.getContext());
                if (!response.equals("valid")) {
                    return null;
                } else {
                Reply originalReply = existingReply.get();

                // Update only the fields that are not null in the updatedReply
                if (updatedReply.getIdUser() != 0) {
                    originalReply.setIdUser(updatedReply.getIdUser());
                }
                if (updatedReply.getContext() != null) {
                    originalReply.setContext(updatedReply.getContext());
                }
                if (updatedReply.getRecommondations() != 0) {
                    originalReply.setRecommondations(updatedReply.getRecommondations());
                }
                if (updatedReply.isVisibility() != originalReply.isVisibility()) {
                    originalReply.setVisibility(updatedReply.isVisibility());
                }

                // Save the updated reply
                return replyRepository.save(originalReply);
                // } else {
                //          return null;
                //      }
            }

        }
            return null; // Handle the case where the reply with the given id doesn't exist

    }
    @Override
    public Re_reply updateRe_reply(String id, Re_reply updatedRe_reply) {
        Optional<Re_reply> existingRe_reply = re_replyRepository.findById(id);

        if (existingRe_reply.isPresent()) {
            String response = chat(updatedRe_reply.getContext());
            if (!response.equals("valid")) {
                return null;
            } else {
                Re_reply originalRe_reply = existingRe_reply.get();

                // Update only the fields that are not null in the updatedReply
                if (updatedRe_reply.getIdUser() != 0) {
                    originalRe_reply.setIdUser(updatedRe_reply.getIdUser());
                }
                if (updatedRe_reply.getContext() != null) {
                    originalRe_reply.setContext(updatedRe_reply.getContext());
                }
                if (updatedRe_reply.getRecommondations() != 0) {
                    originalRe_reply.setRecommondations(updatedRe_reply.getRecommondations());
                }
                if (updatedRe_reply.isVisibility() != originalRe_reply.isVisibility()) {
                    originalRe_reply.setVisibility(updatedRe_reply.isVisibility());
                }

                // Save the updated reply
                return re_replyRepository.save(originalRe_reply);
                // } else {
                //          return null;
                //      }
            }

        }
        return null; // Handle the case where the reply with the given id doesn't exist    }
    }

    @Override
    public void followPost(String idPost, String idUser)throws MessagingException {
        Optional<Post> optionalPost = postRepository.findById(idPost);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();
            if (post.getFollowedBy() == null) {
                post.setFollowedBy(new ArrayList<>());
            }
            // Check if the userId is not already in the followedBy list to avoid duplicates
            if (!post.getFollowedBy().contains(idUser)) {
                post.getFollowedBy().add(idUser);
                followMail();
                postRepository.save(post);
            }
        } else {
            // Handle the case where the Post with the given idPost doesn't exist
            throw new EntityNotFoundException("Post not found with id: " + idPost);
        }
    }
    @Override
    public List<Post> getFollowedPostsByUserId(String userId) {
        return postRepository.getFollowedPostsByUserId(userId);
    }
    @Override
    public void followArticle(String idArticle,String idUser) {
        Optional<Article> optionalPost = articleRepository.findById(idArticle);

        if (optionalPost.isPresent()) {
            Article a = optionalPost.get();
            if (a.getFollowedBy() == null) {
                a.setFollowedBy(new ArrayList<>());
            }
            // Check if the userId is not already in the followedBy list to avoid duplicates
            if (!a.getFollowedBy().contains(idUser)) {
                a.getFollowedBy().add(idUser);
                articleRepository.save(a);
            }
        } else {
            // Handle the case where the Post with the given idPost doesn't exist
            throw new EntityNotFoundException("Article not found with id: " + idArticle);
        }
    }
    @Override
    public void unfollowPost(String idPost, String idUser) {
        Optional<Post> optionalPost = postRepository.findById(idPost);

        if (optionalPost.isPresent()) {
            Post post = optionalPost.get();

            // Remove the userId from the followedBy list if it exists
            post.getFollowedBy().remove(idUser);

            postRepository.save(post);
        } else {
            // Handle the case where the Post with the given idPost doesn't exist
            throw new EntityNotFoundException("Post not found with id: " + idPost);
        }
    }
    @Override
    public void unfollowArticle(String idArticle, String idUser) {
        Optional<Article> optionalPost = articleRepository.findById(idArticle);

        if (optionalPost.isPresent()) {
            Article a = optionalPost.get();

            // Remove the userId from the followedBy list if it exists
            a.getFollowedBy().remove(idUser);

            articleRepository.save(a);
        } else {
            // Handle the case where the Post with the given idPost doesn't exist
            throw new EntityNotFoundException("Article not found with id: " + idArticle);
        }
    }

    @Override
    public List<Post> getFollowedArticlesByUserId(String userId) {
        return articleRepository.getFollowedArticlesByUserId(userId);
    }

    @Override
    public void voteUpPost(String idPost, String userId) {
        Post p = postRepository.findById(idPost).orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + idPost));;
        if (p.getRating() == null) {
            p.setRating(new ArrayList<>());
        }
        // Check if the userId is not already in the followedBy list to avoid duplicates
        if (!p.getRating().contains(userId)) {
            p.getRating().add(userId);
            postRepository.save(p);
        }
    }

    @Override
    public void voteDownPost(String idPost, String userId) {
        Post p = postRepository.findById(idPost) .orElseThrow(() -> new EntityNotFoundException("Post not found with id: " + idPost));;
        // Remove the userId from the followedBy list if it exists
        p.getRating().remove(userId);

        postRepository.save(p);
    }

    @Override
    public void voteUpArticle(String idArticle, String userId) {
        Article a = articleRepository.findById(idArticle).orElseThrow(() -> new EntityNotFoundException("article not found with id: " + idArticle));
        ;
        if (a.getScore() == null) {
            a.setScore(new ArrayList<>());
        }
        // Check if the userId is not already in the followedBy list to avoid duplicates
        if (!a.getScore().contains(userId)) {
            a.getScore().add(userId);
            articleRepository.save(a);
        }
    }
    @Override
    public void voteDownArticle(String idArticle, String userId) {
        Article a = articleRepository.findById(idArticle) .orElseThrow(() -> new EntityNotFoundException("article not found with id: " + idArticle));;
        // Remove the userId from the followedBy list if it exists
        a.getScore().remove(userId);

        articleRepository.save(a);

    }

    @Override
    public boolean deleteReply(String id) {
        if (replyRepository.existsById(id)) {
            replyRepository.deleteById(id);
            return true;
        }
        return false; // Handle the case where the reply with the given id doesn't exist
    }

    @Override
    public boolean deleteRe_reply(String id) {
        if (re_replyRepository.existsById(id)) {
            re_replyRepository.deleteById(id);
            return true;
        }
        return false;    }

    @Override
    public List<Post> getPostsByArticle(String id) {
        return postRepository.getPostsByArticleId(id);
    }


    public String chat(String prompt) {
        try {
            ChatGPTRequest request = new ChatGPTRequest("gpt-3.5-turbo", prompt); // Assuming model is defined elsewhere
            ChatGptResponse chatGptResponse = template.postForObject("https://api.openai.com/v1/chat/completions", request, ChatGptResponse.class);
            return chatGptResponse.getChoices().get(0).getMessage().getContent();
        } catch (Exception e) {

            return null;
        }
    }


}






