package tn.esprit.user.entities;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.mongodb.core.mapping.Document;

@Document
@Getter
@Setter
public class Module {

    private String name;
    private String coefficient ;
    private String credit ;
}
