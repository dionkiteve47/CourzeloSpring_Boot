package tn.esprit.user.entities;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "seats")
public class Seat {
    @Id
    private String _id;
    private String activityId; // Reference to Activity by ID
    private String row;
    private Integer number;
    private Boolean isReserved;

    // Constructors, getters, and setters
    // ...

    public Seat(String _id, String activityId, String row, Integer number, Boolean isReserved) {
        this._id = _id;
        this.activityId = activityId;
        this.row = row;
        this.number = number;
        this.isReserved = isReserved;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getActivityId() {
        return activityId;
    }

    public void setActivityId(String activityId) {
        this.activityId = activityId;
    }

    public String getRow() {
        return row;
    }

    public void setRow(String row) {
        this.row = row;
    }

    public Integer getNumber() {
        return number;
    }

    public void setNumber(Integer number) {
        this.number = number;
    }

    public Boolean getReserved() {
        return isReserved;
    }

    public void setReserved(Boolean reserved) {
        isReserved = reserved;
    }
}
