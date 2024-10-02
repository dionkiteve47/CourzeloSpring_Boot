package tn.esprit.user.entities;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Getter
@Setter
@Document(collection = "vote")
public class Vote {

    String idComment ;
    @Id
    String id;
    int type ;
    String idUser;


}
