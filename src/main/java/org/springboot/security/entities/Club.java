package org.springboot.security.entities;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "club_id")
    private int clubId;

    // Applying the validation on the DTO so no need for validations here
    @Email
    @Column(name = "club_email")
    private String email;
    @Column(name = "club_name")
    private String name;
    @Column(name = "club_description")
    private String description;
    @Column(name = "club_icon")
    private String icon;

    @Column(name = "club_registration_status")
    private String status;

    @JsonIgnore
    @OneToMany(mappedBy = "club",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<ClubMember> clubMembers = new HashSet<>();

    /*
    When serializing an object (e.g., an Event) that has a bidirectional relationship with another object
    (e.g., a Club), the default behavior of many serializers (like Jackson) is to follow both sides of
    the relationship. This can lead to infinite recursion:
    Event -> Club -> Event -> Club ...
    @JsonBackReference tells Jackson to ignore the club field when serializing the Event object, breaking the recursion.

     */
    @JsonIgnoreProperties("club")
    @OneToMany(mappedBy = "club",cascade = CascadeType.ALL)
    private Set<Event> events = new HashSet<>();

    private LocalDate club_registeredAt = LocalDate.now();
    private Date updatedAt;
}
