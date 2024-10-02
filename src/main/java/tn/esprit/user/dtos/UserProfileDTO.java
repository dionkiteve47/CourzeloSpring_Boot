package tn.esprit.user.dtos;

import tn.esprit.user.entities.UserProfile;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserProfileDTO {
    private String name;
    private String lastName;
    private String photo;
    private String speciality;
    private Date birthDate;
    private String title;
    private String bio;
    private String[] skills;

    public UserProfileDTO(UserProfile profile) {
        this.name = profile.getName() != null ? profile.getName() : null;
        this.lastName = profile.getLastName() != null ? profile.getLastName() : null;
        this.photo = profile.getPhoto() != null ? profile.getPhoto().getId() : null;
        this.speciality = profile.getSpeciality() != null ? profile.getSpeciality() : null;
        this.birthDate = profile.getBirthDate() != null ? profile.getBirthDate() : null;
        this.title = profile.getTitle() != null ? profile.getTitle() : null;
        this.bio = profile.getBio() != null ? profile.getBio() : null;
        this.skills = profile.getSkills() != null ? profile.getSkills().toArray(new String[0]) : null;
    }
}
