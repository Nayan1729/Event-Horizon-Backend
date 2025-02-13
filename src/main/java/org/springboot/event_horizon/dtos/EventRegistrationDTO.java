package org.springboot.event_horizon.dtos;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;
import org.springboot.event_horizon.entities.Speaker;
import org.springframework.web.multipart.MultipartFile;

import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;

@Getter
@Setter
public class EventRegistrationDTO {

    @NotBlank(message = "Title is required and cannot be empty.")
    @Size(max = 100, message = "Title cannot exceed 100 characters.")
    private String title;

    @Size(max = 500, message = "Description cannot exceed 500 characters.")
    private String description;

    private MultipartFile image;

    @NotBlank(message = "Location is required and cannot be empty.")
    @Size(max = 200, message = "Location cannot exceed 200 characters.")
    private String location;

    @NotNull(message = "Date is required.")
    @Future(message = "Event date must be in the future.")
    private LocalDate date;

    @NotNull(message = "Start time is required.")
    private LocalTime startTime;

    @NotNull(message = "End time is required.")
    private LocalTime endTime;

    @NotNull(message = "Total registrations can't be null")
    private int totalRegistrations;

    private List<Speaker> speakers; // No validation as requested

    int id;

    private String status;
}
