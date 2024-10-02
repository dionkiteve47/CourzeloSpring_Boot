package tn.esprit.user.entities;


import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document(collection = "Exam")
@ToString
public class Exam {

    @Id
    private String id ;
    private String titre;
    private Date date ;


}
