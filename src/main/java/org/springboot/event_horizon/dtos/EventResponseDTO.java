package org.springboot.event_horizon.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
public class EventResponseDTO {
    private int id;
    private String title;
    private String description;
    private String imageUrl;
    private String status;
    private int clubId;
    private String clubName;
    private String location;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int totalRegistrations;
    private int completedRegistrations;
    private boolean isRegistered;
}
