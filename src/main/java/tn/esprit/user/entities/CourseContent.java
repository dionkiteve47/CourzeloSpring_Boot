package tn.esprit.user.entities;

import tn.esprit.user.entities.enumeration.CoursContentType;
import jakarta.persistence.Id;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;


@Getter
@Setter
@Document(collection = "courseContent")
public class CourseContent {

    @Id
    private String id ;
    private String coursId ;
    private String nom ;
    private String description ;
    private CoursContentType type ;
    private String path ;
    private String fichierName ;
    private String extension ;
    private byte[] data ;
}
