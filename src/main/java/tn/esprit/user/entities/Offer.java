package tn.esprit.user.entities;

import jakarta.persistence.Id;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextScore;

import java.util.List;

@NoArgsConstructor
@Data
@Document(collection = "offers")
public class Offer {
    @Id
    private String id;
    @TextIndexed(weight = 1)
    private String title;
    @TextIndexed(weight = 2)
    private String description;
    @TextIndexed(weight = 1)
    private String location;
    @TextIndexed(weight = 1)
    private String company;
    private String salary;
    @TextIndexed(weight = 1)
    private String type;
    @TextIndexed(weight = 1)
    private String experience;
    @TextIndexed(weight = 2)
    private List<String> skills;
    private String postedOn;
    private String deadline;
    @DBRef
    private User user;
    @TextScore
    private Float score;
}

