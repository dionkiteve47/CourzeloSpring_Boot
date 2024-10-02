package tn.esprit.user.entities;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;
import org.springframework.data.mongodb.core.mapping.MongoId;

import java.util.Date;
import java.util.Objects;
import java.util.UUID;

@Getter
@Setter
@Document(collection = "Commentaire")
@ToString

public class Commentaire {

    @Id

    private  String id ;
    private String comment ;
    private String idUser;
    private String idPost ;
    private String idPere ;
    private Date date ;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Commentaire that)) return false;
        return Objects.equals(id, that.id) && Objects.equals(comment, that.comment) && Objects.equals(idUser, that.idUser) && Objects.equals(idPost, that.idPost) && Objects.equals(idPere, that.idPere) && Objects.equals(date, that.date);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id, comment, idUser, idPost, idPere, date);
    }


}
