package org.springboot.security.dtos;

import lombok.Data;

@Data
public class RegisterForEventDTO {
    private int id;
    private String semester;
    private String customAnswer;

    private String userEmail;
    private String eventTitle;
}
