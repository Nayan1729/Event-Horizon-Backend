package org.springboot.event_horizon.dtos;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class AddClubMembersRequestDTO {
    // Also add the icon
    private String email;
    private String designation ;
}
