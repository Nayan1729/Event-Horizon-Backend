package org.springboot.security.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.hibernate.validator.constraints.UniqueElements;

import java.time.LocalDate;
import java.time.LocalTime;

@Getter
@Setter
@ToString
@Entity
@Table(name = "events")
public class Event {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;


    @NotNull(message = "Event title is required.")
    @Column(name = "event_title", nullable = false)
    private String title;

    @NotNull(message = "Event description is required.")
    @Column(name = "event_description", nullable = false)
    private String description;

    @NotNull(message = "Event location is required.")
    @Column(name = "event_location", nullable = false)
    private String location;

    @NotNull(message = "Event date is required.")
    @Column(name = "event_date", nullable = false)
    private LocalDate date;

    @NotNull(message = "Event start time is required.")
    @Column(name = "start_time", nullable = false)
    private LocalTime startTime;

    @NotNull(message = "Event end time is required.")
    @Column(name = "end_time", nullable = false)
    private LocalTime endTime;


    @JsonIgnoreProperties("events") // Amazing so no need of jsonManaged or backReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "club_id")
    private Club club;
}
