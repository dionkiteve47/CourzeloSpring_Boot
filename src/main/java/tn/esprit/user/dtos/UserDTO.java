package tn.esprit.user.dtos;

import tn.esprit.user.entities.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import tn.esprit.user.entities.*;

import java.util.List;
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {
    private String id;
    private String email;
    private List<String> roles;
    private UserSecurityDTO security;
    private UserProfileDTO profile;
    private UserEducationDTO education;
    private UserContactDTO contact;
    private UserActivityDTO activity;
    private UserSettingsDTO settings;
    private Float score;

    public UserDTO(
            String id,
            String email,
            List<String> list,
            UserSecurity security,
            UserProfile profile,
            UserEducationalDetails education,
            UserContact contact,
            UserActivity activity,
            UserSettings settings,
            Float score
    )
    {
        this.id = id;
        this.email = email;
        this.roles = list;
        this.security = new UserSecurityDTO(security);
        this.profile = new UserProfileDTO(profile);
        this.education = new UserEducationDTO(education);
        this.contact = new UserContactDTO(contact);
        this.activity = new UserActivityDTO(activity);
        this.settings = new UserSettingsDTO(settings);
        this.score = score!=null?score:0.0f;

    }
}
