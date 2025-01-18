package org.springboot.event_horizon.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "votes")
public class Vote {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @ManyToOne
    @JoinColumn(name = "poll_id", nullable = false)
    @JsonIgnoreProperties("votes")
    private Poll poll;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    @JsonIgnoreProperties("votes")
    private User user;

    @Column(name = "voted_event_name", nullable = false)
    private String votedEventName; // Name of the event option selected by the user

    @Override
    public String toString() {
        return "Vote{" +
                "id=" + id +
                '}';
    }

    // Match 2 columns to reach the correct user
    // 1st is the poll where they have made the vote
    // 2nd is the option that they have voted for
}


