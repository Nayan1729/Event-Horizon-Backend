package org.springboot.security.entities;

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
    private int id ;

    @ManyToOne
    @JoinColumn(name = "club_id" , nullable = false)
    private Club club ;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user ;

    @NotNull
    String designation ;

    private LocalDateTime joinedAt  = LocalDateTime.now() ;
}
