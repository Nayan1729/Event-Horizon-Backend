package org.springboot.event_horizon.dtos;

import lombok.Data;

import java.util.List;

@Data
public class ClubDetailsDTO {
    private int clubId;
    private String name;
    private String description;
    private String icon;
    private int membersCount;
    private int eventsCount;
    private String email;
    private List<ClubMemberDTO> members;
    private List<EventResponseDTO> eventsDTO;
}
