package tn.esprit.user.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;

@Document(collection = "notifications")
@Data
public class Notification {

    @Id
    private String _id;
    private String userId;
    private String title;
    private String message;
    private LocalDateTime createdAt;

    // getters and setters

}
