package tn.esprit.user.entities;

import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;

@Getter
@Setter
@Document(collection = "Assignment")
public class Assignment {

    @Id
    private String id ;
    private String coursId;
    private String studnetId;
    private String description ;
    private Date deadline ;
    private String type ;


}
