package tn.esprit.user.dtos;

import tn.esprit.user.entities.UserSecurity;
import lombok.AllArgsConstructor;
import lombok.Data;

@Data
@AllArgsConstructor
public class UserSecurityDTO {
    private boolean twoFactorAuthEnabled;
    private boolean enabled;
    private Boolean ban;
    private boolean rememberMe;

    public UserSecurityDTO(UserSecurity security) {
        this.twoFactorAuthEnabled = security.isTwoFactorAuthEnabled();
        this.enabled = security.isEnabled();
        this.ban = security.getBan();
        this.rememberMe = security.isRememberMe();
    }
}
