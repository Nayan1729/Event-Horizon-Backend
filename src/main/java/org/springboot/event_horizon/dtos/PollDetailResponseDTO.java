package org.springboot.event_horizon.dtos;

import lombok.Data;

import java.util.Map;

@Data
public class PollDetailResponseDTO {
    private int id;
    private String question;
    private Map<String, Integer> eventOptions; // Event name -> Vote count
}

