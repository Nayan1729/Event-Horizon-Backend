package org.springboot.event_horizon.dtos;

import lombok.Data;

import java.util.Map;

@Data
public class PollResponseDTO {
    private int id;
    private String question;
    private Map<String, String> eventOptions; // Event name -> Description
    private Map<String, Integer> voteCount; // Event name -> Vote count
}
