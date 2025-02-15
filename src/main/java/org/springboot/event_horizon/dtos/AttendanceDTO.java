package org.springboot.event_horizon.dtos;

import lombok.Data;

@Data
public class AttendanceDTO {
    private int id;
    private String name;
    private String email;
    private String title;
    private String imageUrl;
    private boolean attendanceStatus; // true or false meaning attending or pending
}