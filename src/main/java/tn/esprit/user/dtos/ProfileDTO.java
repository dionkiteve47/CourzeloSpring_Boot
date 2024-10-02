package tn.esprit.user.dtos;

import lombok.Data;

import java.util.Date;

@Data
public class ProfileDTO {

    private String name;

    private String lastName;
    private String title;
    private String bio;
    private Date birthDate;
}
