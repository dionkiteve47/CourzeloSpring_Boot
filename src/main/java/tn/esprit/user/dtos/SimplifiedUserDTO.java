package tn.esprit.user.dtos;

import tn.esprit.user.entities.Role;
import lombok.Data;

import java.util.List;

@Data
public class SimplifiedUserDTO {
    private String id;
    private String email;
    private List<Role> roles;
    private UserProfileDTO profile;
    private UserEducationDTO education;
    private UserContactDTO contact;
}
