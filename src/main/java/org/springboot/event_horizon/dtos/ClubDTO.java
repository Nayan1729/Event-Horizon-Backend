package org.springboot.event_horizon.dtos;

import lombok.Data;

@Data
public class ClubDTO {
    private int clubId;
    private String name;
    private String description;
    private String icon;
    private int membersCount;
    private int eventsCount;
}
