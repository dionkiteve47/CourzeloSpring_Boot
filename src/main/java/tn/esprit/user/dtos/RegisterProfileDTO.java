package tn.esprit.user.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public class RegisterProfileDTO {
    @NotNull(message = "Name cannot be null")
    @Size(min = 2, message = "Name must be at least 2 characters long")
    private String name;

    @NotNull(message = "Last name cannot be null")
    @Size(min = 2, message = "Last name must be at least 2 characters long")
    private String lastname;
}

