package tn.esprit.user.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class OfferDTO {
    private String id;
    private String title;
    private String description;
    private String location;
    private String company;
    private String salary;
    private String type;
    private String experience;
    private List<String> skills;
    private String postedOn;
    private String deadline;
    private String user;
    private Float score;
}
