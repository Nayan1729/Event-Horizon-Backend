package org.springboot.event_horizon.dtos;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class RegisterClubRequest {
    @NotNull
    private String name;
    @NotNull
    private String description;

    // Add the icon
}
