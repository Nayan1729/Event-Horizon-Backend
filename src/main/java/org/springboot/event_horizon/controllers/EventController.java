package org.springboot.event_horizon.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springboot.event_horizon.dtos.EventResponseDTO;
import org.springboot.event_horizon.entities.Event;
import org.springboot.event_horizon.services.EventService;
import org.springboot.event_horizon.utilities.ApiException;
import org.springboot.event_horizon.utilities.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequiredArgsConstructor
@RestController
public class EventController {

    private final EventService eventService;

    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PostMapping("/clubs/{clubId}/events")
    public ResponseEntity<ApiResponse> createEvent(@PathVariable int clubId,@RequestBody @Valid Event event) {
        try{
            Event registeredEvent = eventService.registerEvent(clubId, event);
            return ResponseEntity.status(201).body(new ApiResponse(201,registeredEvent,"Event registered successfully"));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @DeleteMapping("/clubs/{clubId}/events/{eventId}")
    public ResponseEntity<ApiResponse> deleteEvent(@PathVariable int clubId , @PathVariable int eventId) {
        try{
                this.eventService.deleteEvent(clubId,eventId);
                return ResponseEntity.status(200).body(new ApiResponse(204,clubId,"Event deleted successfully"));
        }catch (ApiException e ){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }


    @GetMapping("events/{eventId}")
    public ResponseEntity<ApiResponse> getEvent(@PathVariable int eventId){
        try{
            EventResponseDTO event  = this.eventService.getEvent(eventId);
            System.out.println(event);

            return ResponseEntity.status(200).body(new ApiResponse(200,event,"Event fetched successfully"));
        }catch (ApiException e ){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @GetMapping("/events")
    public ResponseEntity<ApiResponse> getAllUpcomingEvents() {
        try {
            List<EventResponseDTO> upcomingEvents =  this.eventService.getAllUpcomingEvents();
            System.out.println(upcomingEvents);
            if(upcomingEvents.isEmpty()){
                throw new ApiException("No upcoming events found" , 404);
            }
            return ResponseEntity.status(200).body(new ApiResponse(200,upcomingEvents,"Upcoming events fetched successfully"));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @PostMapping("/events/{eventId}/{eventStatus}")
    public ResponseEntity<ApiResponse> updateEventStatus(@PathVariable String eventStatus, @PathVariable int eventId){
        try {
            this.eventService.changeEventStatus(eventId,eventStatus);
            return ResponseEntity.status(200).body(new ApiResponse(200,eventId,"Event status changed successfully"));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }
}
