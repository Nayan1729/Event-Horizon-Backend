package org.springboot.event_horizon.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ClubMemberDTO{
    private int id;
    private String name;
    private String email;
    private String  designation;
    private LocalDateTime joinedAt;
}
