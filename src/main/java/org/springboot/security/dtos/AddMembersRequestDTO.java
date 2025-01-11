package org.springboot.security.dtos;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.springboot.security.entities.ClubMembers;

import java.util.List;

@Getter
@Setter
public class AddMembersRequestDTO{
    @NotNull
    @Email(message = "Enter a valid email address")
    private String email;

    @NotNull
    private String designation;
}