package org.springboot.event_horizon.dtos;

import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.util.Map;

@Data
public class PollRequestDTO {
    @NotNull(message = "Poll question is required")
    private String question;

    @Size(min = 2, message = "A poll must have at least 2 event options")
    private Map<String, String> eventOptions; // Key: Event name, Value: Event description
}

