package org.springboot.security.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Getter
@Setter
@Entity
public class Club {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int club_id;

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

    @JsonIgnore
    @OneToMany(mappedBy = "club",cascade = CascadeType.ALL,fetch = FetchType.LAZY)
    private Set<ClubMembers> clubMembers = new HashSet<>();

    private LocalDate club_registeredAt = LocalDate.now();
}
