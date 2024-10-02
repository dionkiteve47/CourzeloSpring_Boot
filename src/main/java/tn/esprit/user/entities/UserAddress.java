package tn.esprit.user.entities;

import lombok.Data;
import org.springframework.data.mongodb.core.index.TextIndexed;

@Data
public class UserAddress {
    private String address;
    private String city;
    private String state;
    @TextIndexed
    private String country;
    private String zipCode;
}
