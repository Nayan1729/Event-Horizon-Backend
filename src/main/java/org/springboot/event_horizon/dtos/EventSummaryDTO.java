package org.springboot.event_horizon.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventSummaryDTO{
    private int id;
    private String imageUrl;
    private String title;
    private String description;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private int totalRegistrations;
    private int totalAttendance;
}