package tn.esprit.user.entities;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;
import lombok.*;

import java.util.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
@Document(collection = "Cours")
public class Cours {
    @Id
  //  @JsonIgnore
    private String id_cours;
    private String nomCours;
    private String nomProfesseur;
    private TypeC typeCours;
    private Date dateInscription;
    private String descriptionCours;
    private Float prix;
    private String photo;
    @DBRef
    @JsonIgnore
    private Matiere matiere;

    @DBRef
    private List<Ressource> ressourceList=new ArrayList<>();

    @Column(columnDefinition = "int default 0")
    private int likes;

    @Column(columnDefinition = "int default 0")
    private int dislikes;

    @ElementCollection
    private Set<String> likedBy = new HashSet<>();

    @ElementCollection
    private Set<String> dislikedBy = new HashSet<>();

}
