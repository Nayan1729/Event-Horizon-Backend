package org.springboot.security.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@Setter
@ToString
public class EventResponseDTO {
    private int id;
    private String title;
    private String description;
    private String status;
    private int clubId;
    private String clubName;
}
