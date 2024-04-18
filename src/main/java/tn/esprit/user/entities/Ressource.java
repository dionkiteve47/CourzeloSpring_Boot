package tn.esprit.user.entities;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document("Ressource")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class Ressource {
    @Id
    private String idRessource;
    private String nomRessource;
    private String photo;
    @DBRef
    private Cours cours;
    private String video;
    @Getter
    private String originalVideoName;  // New field for storing the original video name


    public void setVideo(String video) {
        this.video = video;
    }

    public void setOriginalVideoName(String originalVideoName) {
        this.originalVideoName = originalVideoName;
    }

}
