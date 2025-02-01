package org.springboot.event_horizon.dtos;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springboot.event_horizon.entities.RegisterForEvent;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@ToString
public class RegistrationDTO{
    private int id ;
    private String title;
    private LocalDate  date ;
    List<RegisterForEventDTO> pendingRegistrations;
}
