package org.springboot.event_horizon.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AddMembersRequestDTO{
    @NotNull
    @Email(message = "Enter a valid email address")
    private String email;

    @NotNull(message = "Designation is required")
    private String designation;
}