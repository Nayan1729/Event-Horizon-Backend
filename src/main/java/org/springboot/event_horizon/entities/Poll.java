package org.springboot.event_horizon.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Data;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

@Data
@Table(name = "polls")
@Entity
public class Poll {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Column(nullable = false)
    private String question; // Example: "Which event should we organize next?"

    @ElementCollection
    @CollectionTable(name = "poll_event_options", joinColumns = @JoinColumn(name = "poll_id"))
    @MapKeyColumn(name = "event_name") // Key of the map: Event name
    @Column(name = "event_description") // Value of the map: Event description
    private Map<String, String> eventOptions = new HashMap<>();

    @OneToMany(mappedBy = "poll", cascade = CascadeType.ALL, fetch = FetchType.LAZY)
    @JsonIgnoreProperties("poll")
    private Set<Vote> votes = new HashSet<>();
}


