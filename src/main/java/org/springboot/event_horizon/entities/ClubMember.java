package org.springboot.event_horizon.entities;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDateTime;


@Getter
@Setter
@ToString
@Entity
public class ClubMember {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id ; //

    @JsonIgnoreProperties("clubMembers")
    @ManyToOne
    @JoinColumn(name = "club_id" , nullable = false)
    private Club club ;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user ; // name , email

    @NotNull
    String designation; //

    private LocalDateTime joinedAt  = LocalDateTime.now() ; //
}
