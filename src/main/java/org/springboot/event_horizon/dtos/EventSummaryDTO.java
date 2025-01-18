package org.springboot.event_horizon.dtos;

import lombok.*;

import java.time.LocalDate;
import java.time.LocalTime;

@Data
public class EventSummaryDTO{
    private int id;
    private String title;
    private LocalDate date;
    private LocalTime startTime;
    private LocalTime endTime;
    private String status;
    private int pendingRegistrations;
}