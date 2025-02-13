package org.springboot.event_horizon.entities;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import lombok.Getter;
import lombok.Setter;

import java.util.Set;

@Getter
@Setter
@Entity
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private int id;

    @Column(unique = true, nullable = false,name = "user_email")
    @Email(message = "Enter a valid email")
    private String email;

    private String name;

    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    @NotNull(message = "Password can't be null")
    private String password;

    @JsonIgnore
    private String verificationToken;  // To store the email verification token
    @JsonIgnore
    private boolean verified;  // To check if the user is verified

    @JsonIgnore
    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(
            name = "user_roles", // Join table
            joinColumns = @JoinColumn(name = "user_id"),
            inverseJoinColumns = @JoinColumn(name = "role_id")
    )
    private Set<Role> roles;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL,mappedBy = "user")
    private Set<ClubMember> clubMembers;

    @JsonIgnore
    @OneToMany(cascade = CascadeType.ALL, mappedBy = "user")
    private Set<RegisterForEvent> eventRegitrations ;

    @JsonIgnore
    @OneToMany(mappedBy = "user")
    private Set<Vote> votes;

    private String imageUrl;

    @Override
    public String toString() {
        return "User{" +
                ", email='" + email + '\'' +
                ", password='" + password + '\'' +

                '}';
    }
}
