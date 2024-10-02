package tn.esprit.user.entities;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "searches")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Search {

    @Id
    private String id;
    private String query;
    private int count =0;

    public Search(String query) {
        this.query = query;
    }
}
