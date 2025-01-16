    package org.springboot.security.entities;

    import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
    import jakarta.persistence.*;
    import jakarta.validation.constraints.NotNull;
    import lombok.Getter;
    import lombok.Setter;
    import lombok.ToString;

    import java.time.LocalDate;
    import java.time.LocalTime;
    import java.util.Set;

    @Getter
    @Setter
    @ToString
    @Entity
    @Table(name = "events")
    public class Event {
        @Id

        @Column(name = "id")@GeneratedValue(strategy = GenerationType.IDENTITY)
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

        @Column(name = "event_status" , nullable = false)
        private String status = "UPCOMING";


        @JsonIgnoreProperties("events") // Amazing so no need of jsonManaged or backReference
        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "club_id")
        private Club club;

        @OneToMany( cascade = CascadeType.ALL,mappedBy ="event")
        @JsonIgnoreProperties("event")
        private Set<RegisterForEvent> registerForEvents;
    }
