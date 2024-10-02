package tn.esprit.user.dtos;

import tn.esprit.user.entities.UserSettings;
import lombok.Data;

@Data
public class UserSettingsDTO {
    boolean showPhone;
    boolean showAddress;
    boolean showBirthDate;

    public UserSettingsDTO(UserSettings settings) {
        this.showPhone = settings.isShowPhone();
        this.showAddress = settings.isShowAddress();
        this.showBirthDate = settings.isShowBirthDate();

    }
}
