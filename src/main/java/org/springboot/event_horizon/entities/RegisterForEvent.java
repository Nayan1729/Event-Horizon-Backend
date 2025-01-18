package org.springboot.event_horizon.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Getter
@Setter
public class RegisterForEvent {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    private User user;

    @ManyToOne
    @JsonIgnoreProperties("registerForEvents")
    private Event event;

    private int semester;

    private String customAnswer;

    private String status = "PENDING"; // UNAPPROVED  , APPROVED , PENDING

    private LocalDateTime registeredAt = LocalDateTime.now();
}

