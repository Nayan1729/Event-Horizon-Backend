package org.springboot.event_horizon.dtos;

import lombok.Data;

import java.time.LocalDate;

@Data
public class ClubRequestDTO {
    private int clubId;
    private String name;
    private String email;
    private String description;
    private LocalDate club_registeredAt;
    private String userName;
}
