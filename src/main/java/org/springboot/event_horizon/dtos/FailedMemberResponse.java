package org.springboot.event_horizon.dtos;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class FailedMemberResponse {
    private String email;
    private String error;
}
