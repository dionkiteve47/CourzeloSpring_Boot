package tn.esprit.user.entities;

import lombok.Data;

import java.time.Instant;

@Data
public class UserActivity {
    private Instant CreatedAt;
    private Instant updatedAt;
    private Instant lastLogin;
    private Instant lastLogout;
    private Instant lastPasswordChange;
    private Instant lastEmailChange;
    private Instant lastContactChange;
    private Instant lastSecurityChange;
    private Instant lastProfileChange;
    private int loginCount;

}
