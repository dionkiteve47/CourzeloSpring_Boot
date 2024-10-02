package tn.esprit.user.entities;


import lombok.Builder;
import org.springframework.data.mongodb.core.mapping.Document;
@Document(collection = "userHamzas")
@Builder
public class UserHamza {
    private String _id;
    private String userName;
   /* @OneToMany(mappedBy = "club")
    private List<Activity> activities;*/

    public UserHamza() {
    }

    public UserHamza(String userName) {
        this.userName = userName;
    }
/*  public Club(String _id, String clubName, List<Activity> activities) {
        this._id = _id;
        this.clubName = clubName;
        this.activities = activities;
    }*/

    public UserHamza(String _id, String userName) {
        this._id = _id;
        this.userName = userName;
    }

    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    @Override
    public String toString() {
        return "UserHamza{" +
                "_id='" + _id + '\'' +
                ", userName='" + userName + '\'' +
                '}';
    }
}
