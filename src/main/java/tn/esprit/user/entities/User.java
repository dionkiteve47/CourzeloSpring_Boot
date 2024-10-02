package tn.esprit.user.entities;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextScore;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Objects;
@NoArgsConstructor
@Data
@Document(collection = "users")
public class User implements UserDetails {
    @Id
    private String id;
    @NotNull
    @TextIndexed(weight = 1)
    private String email;
    @NotNull
    private String password;
    @NotNull
    @TextIndexed(weight = 2)
    private List<Role> roles = new ArrayList<>();
    private UserSecurity security = new UserSecurity();
    @TextIndexed(weight = 3)
    private UserProfile profile = new UserProfile();
    @TextIndexed
    private UserEducationalDetails education = new UserEducationalDetails();
    private UserContact contact = new UserContact();
    private UserActivity activity = new UserActivity();
    private UserSettings settings = new UserSettings();
    @TextScore
    private Float score;

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return roles.stream().map(role1 -> new SimpleGrantedAuthority("ROLE_" + role1.name())).toList();
    }

    public User(String email, String password, String name, String lastName) {
        this.email = email;
        this.password = password;
        this.profile.setName(name);
        this.profile.setLastName(lastName);
    }

    @Override
    public String getUsername() {
        return email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !security.getBan();
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return security.isEnabled();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        User user = (User) o;
        return Objects.equals(id, user.id) &&
                Objects.equals(email, user.email);
    }
}
