package tn.esprit.user.dtos;

import tn.esprit.user.entities.UserAddress;
import tn.esprit.user.entities.UserContact;
import lombok.Data;

@Data
public class UserContactDTO {
    private UserAddress userAddress;
    private String phoneNumber;
    private String website;
    private String linkedin;
    private String facebook;
    private String github;

    public UserContactDTO(UserAddress userAddress, String phoneNumber, String website, String linkedin, String facebook, String github) {
        this.userAddress = userAddress;
        this.phoneNumber = phoneNumber;
        this.website = website;
        this.linkedin = linkedin;
        this.facebook = facebook;
        this.github = github;
    }

    public UserContactDTO(UserContact contact) {
        this.userAddress = contact.getUserAddress();
        this.phoneNumber = contact.getPhoneNumber();
        this.website = contact.getWebsite();
        this.linkedin = contact.getLinkedin();
        this.facebook = contact.getFacebook();
        this.github = contact.getGithub();
    }
}
