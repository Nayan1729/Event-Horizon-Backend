package org.springboot.event_horizon.dtos;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.web.multipart.MultipartFile;

@Getter
@Setter
@ToString
public class RegisterClubRequest {
    @NotNull
    private String name;
    @NotNull
    private String description;

    private MultipartFile icon;
}
