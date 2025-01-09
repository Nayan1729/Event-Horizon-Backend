package org.springboot.security.entities;

import jakarta.persistence.*;

import java.util.Date;

@Entity
public class ClubRequest {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    private String name; // Club name
    private String description;
    private String email; // Requester's email
    private String status; // PENDING, APPROVED, REJECTED

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt = new Date();

    @Temporal(TemporalType.TIMESTAMP)
    private Date updatedAt;
}
