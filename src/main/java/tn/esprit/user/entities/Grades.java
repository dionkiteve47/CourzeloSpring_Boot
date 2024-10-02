package tn.esprit.user.entities;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document(collection = "Grades")
public class Grades {


    @Id
    private String id ;

    private Float note ;

    private String idExamen ;
    private String idEtudiant ;
    private Date datePass ;
}
