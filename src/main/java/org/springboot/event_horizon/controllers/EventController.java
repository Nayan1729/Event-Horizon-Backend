package org.springboot.event_horizon.controllers;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springboot.event_horizon.dtos.EventResponseDTO;
import org.springboot.event_horizon.entities.Club;
import org.springboot.event_horizon.entities.Event;
import org.springboot.event_horizon.entities.User;
import org.springboot.event_horizon.services.ClubService;
import org.springboot.event_horizon.services.EventService;
import org.springboot.event_horizon.services.UserService;
import org.springboot.event_horizon.utilities.ApiException;
import org.springboot.event_horizon.utilities.ApiResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

import java.util.List;


@RequestMapping("/api/v1/events")
@RequiredArgsConstructor
@RestController
public class EventController {

    private final EventService eventService;
    private final UserService userService;
    private final ClubService clubService;


    @GetMapping
    public ResponseEntity<ApiResponse> getAllEvents() {
        try {
            List<EventResponseDTO> allEvents =  this.eventService.getAllEvents();
            System.out.println(allEvents);
            if(allEvents.isEmpty()){
                throw new ApiException("No events found" , 404);
            }
            return ResponseEntity.status(200).body(new ApiResponse(200,allEvents,"All events fetched successfully"));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PostMapping
    public ResponseEntity<ApiResponse> createEvent(@RequestBody @Valid Event event){
        try{
            User currentUser = userService.getLoggedInUser();
            int clubId = clubService.getClubIdByEmail(currentUser.getEmail());
            Event registeredEvent = this.eventService.registerEvent(clubId, event);
            return ResponseEntity.status(201).body(new ApiResponse(201,registeredEvent,"Event registered successfully"));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @DeleteMapping("/{eventId}")
    public ResponseEntity<ApiResponse> deleteEvent( @PathVariable int eventId){
        try{
            this.eventService.deleteEvent(eventId);
            return ResponseEntity.status(200).body(new ApiResponse(204,null,"Event  deleted successfully"));
        }catch (ApiException e ){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }
    @GetMapping("/{eventId}")
    public ResponseEntity<ApiResponse> getEvent(@PathVariable int eventId){
        try{
            System.out.println("Hello");
            EventResponseDTO event  = this.eventService.getEvent(eventId);
            System.out.println(event);

            return ResponseEntity.status(200).body(new ApiResponse(200,event,"Event fetched successfully"));
        }catch (ApiException e ){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }

    @PreAuthorize("hasRole('CLUB_ADMIN')")
    @PostMapping("/{eventId}/{eventStatus}")
    public ResponseEntity<ApiResponse> updateEventStatus(@PathVariable String eventStatus, @PathVariable int eventId){
        try {
            this.eventService.changeEventStatus(eventId,eventStatus);
            return ResponseEntity.status(200).body(new ApiResponse(200,eventId,"Event status changed successfully"));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }
    @PreAuthorize(("hasRole('CLUB_ADMIN')"))
    @PatchMapping
    public ResponseEntity<ApiResponse> updateEvent(@RequestBody @Valid Event event){
        try{
            int id = this.eventService.updateEvent(event);
            return ResponseEntity.status(200).body(new ApiResponse(200,id,"Event updated successfully..."));
        }catch (ApiException e){
            return ResponseEntity.status(e.getStatusCode()).body(new ApiResponse(e.getStatusCode(), null, e.getMessage()));
        }
    }
}
