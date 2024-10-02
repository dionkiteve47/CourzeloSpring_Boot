package tn.esprit.user.entities;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.DBRef;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "reservations")
public class Reservation {
    @Id
    private String id;

    @DBRef
    private Seat seat; // Reference to Seat

    @DBRef
    // houni
    private UserHamza user; // Reference to User, assuming User is another domain object

    // Constructors, getters, and setters
    // ...


    public Reservation(String id, Seat seat, UserHamza user) {
        this.id = id;
        this.seat = seat;
        this.user = user;
    }

    public Reservation() {

    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Seat getSeat() {
        return seat;
    }

    public void setSeat(Seat seat) {
        this.seat = seat;
    }

    public UserHamza getUser() {
        return user;
    }

    public void setUser(UserHamza user) {
        this.user = user;
    }
}
